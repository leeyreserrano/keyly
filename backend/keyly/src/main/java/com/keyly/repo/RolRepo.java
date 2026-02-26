package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keyly.model.Rol;

public interface RolRepo extends JpaRepository<Rol, Long> {
    
}
