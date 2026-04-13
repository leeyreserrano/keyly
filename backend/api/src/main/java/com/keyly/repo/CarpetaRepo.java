package com.keyly.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;

import com.keyly.model.Carpeta;
import com.keyly.model.Usuari;

import jakarta.transaction.Transactional;

@Repository
public interface CarpetaRepo extends JpaRepository<Carpeta, Long> {

    Optional<Carpeta> findByUuid(UUID uuid);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM Carpetes_Items ci
                JOIN Items i ON i.id = ci.item_id
                WHERE i.uuid = :uuid
            )
            """, nativeQuery = true)
    Long existItemInCarpetes(UUID uuid);

    List<Carpeta> findByBagulPropietariUuid(@Param("uuid") UUID uuid);

    Optional<Carpeta> findByBagulPropietariAndUuid(@Param("usuari") Usuari usuari, @Param("uuid") UUID uuid);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);

}
