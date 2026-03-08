package com.keyly.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Departament;
import java.util.UUID;


@Repository
public interface DepartamentRepo extends JpaRepository<Departament, Long> {

    Optional<Departament> findByUuid(UUID uuid);

    Optional<Departament> deleteByUuid(UUID uuid);
    
}
