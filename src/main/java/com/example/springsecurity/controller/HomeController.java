package com.example.springsecurity.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.springsecurity.model.OrdemModel;
import com.example.springsecurity.repository.OrdemRepository;

@RestController
@RequestMapping("/")
public class HomeController {

    @Autowired
    private OrdemRepository ordemRepository;

    @GetMapping("/login")
    public String login() {
        return "/dashboard";
    }

    @GetMapping("/dashboard")
    public ModelAndView Dashboard() {
        ModelAndView modelAndView = new ModelAndView("dashboard");
        return modelAndView;
    }

    @RequestMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:login";
    }

    @GetMapping("/cadastro")
    public ModelAndView Cadastro() {
        ModelAndView modelAndView = new ModelAndView("cadastro");
        modelAndView.addObject("ordemrepository", ordemRepository.findAll());
        modelAndView.addObject("ordem", new OrdemModel());
        return modelAndView;
    }

    @PostMapping("/cadastro")
    public String salvar(@ModelAttribute("cadastro") OrdemModel ordem) {
        // salva os dados do formulário no banco de dados usando o repositório
        ordemRepository.save(ordem);
        // redireciona para a página "ordem"
        return "redirect:/login";
    }

}
/*
 * Class.forName("com.mysql.cj.jdbc.Driver");
 * Connection conn = DriverManager.getConnection(
 * "jdbc:mysql://aws.connect.psdb.cloud/gestao_ordem_bd?sslMode=VERIFY_IDENTITY",
 * "exc0qoqtlb1kdaw967o7",
 * "pscale_pw_q5UIy1TKlh6ZmQzfQiNIqjbf048oRpR4tUoyy1tTgJd");
 * 
 */