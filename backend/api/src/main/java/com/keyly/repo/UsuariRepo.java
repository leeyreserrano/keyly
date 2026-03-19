package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.keyly.model.Usuari;

import jakarta.transaction.Transactional;

@Repository
public interface UsuariRepo extends JpaRepository<Usuari, Long> {

    Optional<Usuari> findByUuid(UUID uuid);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);
    
    boolean existsByCorreu(String correu);

}
