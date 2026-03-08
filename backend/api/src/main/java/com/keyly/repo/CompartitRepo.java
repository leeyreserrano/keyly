package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Compartit;
import com.keyly.model.Usuari;

@Repository
public interface CompartitRepo extends JpaRepository<Compartit, Long> {

    Optional<Compartit> findByUuid(UUID uuid);

    Optional<Compartit> deleteByUuid(UUID uuid);

}
