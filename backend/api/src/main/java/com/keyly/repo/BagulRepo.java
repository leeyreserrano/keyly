package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Bagul;

@Repository
public interface BagulRepo extends JpaRepository<Bagul, Long> {

    Optional<Bagul> findByUuid(UUID uuid);

    Optional<Bagul> deleteByUuid(UUID uuid);

}
