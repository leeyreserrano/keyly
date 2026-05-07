package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.EncryptedDataKeys;

public record EncryptedDataKeyResponse(
        UUID uuid,
        String encryptedDataKey) {

    public EncryptedDataKeyResponse(EncryptedDataKeys e) {
        this(e.getUuid(), e.getEncryptedDataKey());
    }
}