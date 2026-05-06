package com.keyly.model.request.combined;

import java.util.UUID;

public record ItemEncryptedDataKey(
    UUID itemUuid,
    String encryptedDataKey
) {}