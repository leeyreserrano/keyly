package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.keyly.model.Bagul;

import jakarta.transaction.Transactional;

@Repository
public interface BagulRepo extends JpaRepository<Bagul, Long> {

    Optional<Bagul> findByUuid(UUID uuid);

    Optional<Bagul> findByPropietariUuid(@Param("uuid") UUID uuid);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);

}
