package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Domini;

@Repository
public interface DominiRepo extends JpaRepository<Domini, Long> {

    Optional<Domini> findByUuid(UUID uuid);

    Optional<Domini> deleteByUuid(UUID uuid);

}
