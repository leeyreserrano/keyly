package com.keyly.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.EncryptedDataKeys;
import com.keyly.model.response.EncryptedDataKeyResponse;

@Repository
public interface EncryptedDataKeysRepo extends JpaRepository<EncryptedDataKeys, Long> {

    EncryptedDataKeyResponse findByItemUuidAndUsuariUuid(UUID uuid, UUID usuariUuid);

    EncryptedDataKeys findByUsuariUuidAndItemUuid(UUID usuariUuid, UUID itemUuid);

    EncryptedDataKeyResponse findAllByItemUuid(UUID uuid);

    void deleteByItemUuidAndUsuariUuid(UUID itemUuid, UUID usuariUuid);

}
