package com.keyly.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.keyly.model.Domini;

import jakarta.transaction.Transactional;

@Repository
public interface DominiRepo extends JpaRepository<Domini, Long> {

    Optional<Domini> findByUuid(UUID uuid);

    @Query("SELECT d FROM Dominis d WHERE d.sucursal.uuid = :sucursalUuid")
    Optional<List<Domini>> findBySucursalUuid(UUID uuid);

    boolean existsByDomini(String domini);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);

}
