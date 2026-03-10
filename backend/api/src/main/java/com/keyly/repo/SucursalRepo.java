package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.keyly.model.Sucursal;

import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface SucursalRepo extends JpaRepository<Sucursal, Long> {

    Optional<Sucursal> findByUuid(UUID uuid);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);

}
