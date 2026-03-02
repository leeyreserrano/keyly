package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Rol;

@Repository
public interface RolRepo extends JpaRepository<Rol, Long> {
    
}
