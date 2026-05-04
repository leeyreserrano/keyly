package com.keyly.model.request;

import java.util.UUID;

import com.keyly.model.enums.Permisos;

public record UsuarisCompartits (
    UUID usuariUuid,
    Permisos permis,
    String encryptedDataKey
) {}