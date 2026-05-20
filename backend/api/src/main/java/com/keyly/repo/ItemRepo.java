package com.keyly.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.keyly.model.Item;
import com.keyly.model.Usuari;

import jakarta.transaction.Transactional;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {

    Optional<Item> findByUuid(UUID uuid);

    List<Item> findByBagulPropietariUuid(@Param("uuid") UUID uuid);

    Optional<Item> findByBagulPropietariAndUuid(@Param("usuari") Usuari usuari,
            @Param("uuid") UUID uuid);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);

}
