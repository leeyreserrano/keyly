package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Rol;

@Repository
public interface RolRepo extends JpaRepository<Rol, Long> {

    Optional<Rol> findByUuid(UUID uuid);

    Optional<Rol> deleteByUuid(UUID uuid);

}
