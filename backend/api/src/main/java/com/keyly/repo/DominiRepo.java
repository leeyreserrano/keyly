package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.keyly.model.Domini;

import jakarta.transaction.Transactional;

@Repository
public interface DominiRepo extends JpaRepository<Domini, Long> {

    Optional<Domini> findByUuid(UUID uuid);

    boolean existsByDomini(String domini);

    @Modifying
    @Transactional
    void deleteByUuid(UUID uuid);

}
