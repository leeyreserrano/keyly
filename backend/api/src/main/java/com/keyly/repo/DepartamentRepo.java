package com.keyly.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.keyly.model.Departament;

import jakarta.transaction.Transactional;

import java.util.UUID;


@Repository
public interface DepartamentRepo extends JpaRepository<Departament, Long> {

    Optional<Departament> findByUuid(UUID uuid);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);
    
}
