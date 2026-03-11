package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Config;

@Repository
public interface ConfigRepo extends JpaRepository<Config, Long> {

    Optional<Config> findByUuid(UUID uuid);

    Optional<Config> findBySucursalId(Long id);

}
