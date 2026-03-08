package com.keyly.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Item;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {

    Optional<Item> findByUuid(UUID uuid);

    Optional<Item> deleteByUuid(UUID uuid);

}
