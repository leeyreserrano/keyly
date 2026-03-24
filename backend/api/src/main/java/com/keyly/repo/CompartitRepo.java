package com.keyly.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.keyly.model.Compartit;
import com.keyly.model.Usuari;

@Repository
public interface CompartitRepo extends JpaRepository<Compartit, Long> {

    Optional<Compartit> findByUuid(UUID uuid);

    @Query("""
                SELECT c
                FROM Compartit c
                JOIN c.usuari u
                WHERE u.uuid = :uuid
            """)
    List<Compartit> findAllByUsuariUuid(@Param("uuid") UUID uuid);

    @Query("""
                SELECT c
                FROM Compartit c
                WHERE c.usuari = :usuari
                  AND c.uuid = :uuid
            """)
    Optional<Compartit> findUserCompartitByUuid(@Param("usuari") Usuari usuari, @Param("uuid") UUID uuid);

    Optional<Compartit> deleteByUuid(UUID uuid);

}
