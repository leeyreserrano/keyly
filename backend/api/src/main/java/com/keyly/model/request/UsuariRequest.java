package com.keyly.model.request;

import java.util.UUID;

public record UsuariRequest(
        UUID sucursalUuid,
        UUID departamentUuid,
        UUID rolUuid,
        String nom,
        String correu,
        String imatge,
        String contrasenya,
        boolean potAdministrar) {

}
