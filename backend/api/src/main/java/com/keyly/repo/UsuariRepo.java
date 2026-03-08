package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Usuari;

@Repository
public interface UsuariRepo extends JpaRepository<Usuari, Long> {

    Optional<Usuari> findByUuid(UUID uuid);

    Optional<Usuari> deleteByUuid(UUID uuid);
    boolean existsByCorreu(String correu);

}
