package com.keyly.model.response;

import com.keyly.model.EncryptedDataKeys;

public record EncryptedDataKeyResponse(
        String uuid,
        String encryptedDataKey) {

    public EncryptedDataKeyResponse(EncryptedDataKeys e) {
        this(e.getUuid().toString(), e.getEncryptedDatakey());
    }
}