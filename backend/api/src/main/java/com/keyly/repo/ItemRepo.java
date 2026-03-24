package com.keyly.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.keyly.model.Item;
import com.keyly.model.Usuari;

import jakarta.transaction.Transactional;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {

    Optional<Item> findByUuid(UUID uuid);

    @Query("""
                SELECT i
                FROM Item i
                WHERE i.bagul.propietari.uuid = :uuid
            """)
    List<Item> findAllByUsuariUuid(@Param("uuid") UUID uuid);

    @Query("""
                SELECT i
                FROM Item i
                JOIN i.bagul b
                JOIN b.propietari u
                WHERE u = :usuari
                  AND i.uuid = :uuid
            """)
    Optional<Item> findUserItemByUuid(@Param("usuari") Usuari usuari,
            @Param("uuid") UUID uuid);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);

}
