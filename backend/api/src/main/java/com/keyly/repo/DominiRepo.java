package com.keyly.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.keyly.model.Domini;

import jakarta.transaction.Transactional;

@Repository
public interface DominiRepo extends JpaRepository<Domini, Long> {

    Optional<Domini> findByUuid(UUID uuid);

    @Query(value = """
            SELECT d.id, d.uuid, d.sucursal_id, d.domini
            FROM Dominis d
            JOIN Sucursals s ON d.sucursal_id = s.id
            WHERE s.uuid = :sucursalUuid
            """, nativeQuery = true)
    Optional<List<Domini>> findBySucursalUuid(@Param("sucursalUuid") UUID sucursalUuid);

    boolean existsByDomini(String domini);

    boolean existsByDominiAndSucursalUuid(String domini, UUID sucursal);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);

}
