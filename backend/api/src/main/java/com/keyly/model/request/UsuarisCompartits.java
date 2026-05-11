package com.keyly.model.request;

import java.util.List;
import java.util.UUID;

import com.keyly.model.enums.Permisos;
import com.keyly.model.request.combined.ItemEncryptedDataKey;

public record UsuarisCompartits (
    UUID usuariUuid,
    Permisos permis,
    List<ItemEncryptedDataKey> encryptedDataKeys
) {}