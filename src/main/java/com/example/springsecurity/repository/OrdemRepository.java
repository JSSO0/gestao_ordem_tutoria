package com.example.springsecurity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

import com.example.springsecurity.model.OrdemModel;

@Repository
public interface OrdemRepository extends JpaRepository<OrdemModel, Long>{
    OrdemModel findByMatricula(String matricula);
}
