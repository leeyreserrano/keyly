package com.keyly.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.EncryptedDataKeys;
import com.keyly.model.response.EncryptedDataKeyResponse;

@Repository
public interface EncryptedDataKeysRepo extends JpaRepository<EncryptedDataKeys, Long> {

    EncryptedDataKeyResponse findByItemUuidAndUsuariUuid(UUID uuid, UUID usuariUuid);

    EncryptedDataKeyResponse findAllByItemUuid(UUID uuid);

}
