package com.keyly.model.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public record UsuariRequest(
        UUID sucursalUuid,
        UUID departamentUuid,
        UUID rolUuid,
        String nom,
        String correu,
        String imatge,
        String contrasenya,
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        Boolean potAdministrar) {

}
