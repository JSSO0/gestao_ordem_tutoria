package com.example.springsecurity.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.springsecurity.model.OrdemModel;
import com.example.springsecurity.repository.OrdemRepository;

@Configuration
@EnableWebSecurity
public class ConfigSpringConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() { // Responsável pelo Encoder do password
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("matricula")
                .permitAll()
                .and()
                .logout().permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager();
        userDetailsManager.setDataSource(dataSource);
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String matricula) throws UsernameNotFoundException {
                List<String> roles = new ArrayList<>();
                String query = "SELECT matricula, password, enabled FROM ordem_model WHERE matricula = ?";
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try (Connection connection = dataSource.getConnection()) {
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, matricula);
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String password = resultSet.getString("password");
                        boolean enabled = resultSet.getBoolean("enabled");
                        roles.add("USER");
                        return new User(resultSet.getString("matricula"), passwordEncoder().encode(password), enabled,
                                true, true, true,
                                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                    } else {
                        throw new UsernameNotFoundException("Matricula não encontrada");
                    }
                } catch (SQLException e) {
                    throw new UsernameNotFoundException("Falha na consulta ao banco de dados");
                } finally {
                    if (resultSet != null)
                        resultSet.close();
                    if (preparedStatement != null)
                        preparedStatement.close();
                }
            }
        };

    }

    @Service
    public class CustomUserDetailsService implements UserDetailsService {

        @Autowired
        private OrdemRepository usuarioRepository;

        @Override
        public UserDetails loadUserByUsername(String matricula) throws UsernameNotFoundException {
            OrdemModel ordem = usuarioRepository.findByMatricula(matricula);
            if (ordem == null) {
                throw new UsernameNotFoundException("Usuário não encontrado.");
            }
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("USER"));
            return new User(ordem.getMatricula(), "", authorities);
        }
    }
    /*
     * @Override
     * protected void configure(AuthenticationManagerBuilder auth) throws Exception
     * { //guarda user e password em memória
     * auth.inMemoryAuthentication()
     * .withUser("joely")
     * .password(passwordEncoder().encode("teste")).authorities("USER");
     * }
     */
}
