package com.keyly.model.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.keyly.model.enums.RolIntern;

public record UsuariRequest(
        UUID sucursalUuid,
        UUID departamentUuid,
        UUID rolUuid,
        String nom,
        String correu,
        String contrasenya,
        String kdfSalt,
        String publicKey,
        String encryptedPrivateKey,
        RolIntern rolIntern,
        @JsonSetter(nulls = Nulls.AS_EMPTY) Boolean potAdministrar) {

}
