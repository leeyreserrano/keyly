package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keyly.model.Departament;

public interface DepartamentRepo extends JpaRepository<Departament, Long> {
    
}
