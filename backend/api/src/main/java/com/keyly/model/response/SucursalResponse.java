package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Sucursal;

public record SucursalResponse(
        UUID uuid,
        String nom,
        String direccio,
        String ciutat,
        String pais,
        String telefon,
        String correu) {

    public SucursalResponse(Sucursal s) {
        this(
                s.getUuid(),
                s.getNom(),
                s.getDireccio(),
                s.getCiutat(),
                s.getPais(),
                s.getTelefon(),
                s.getCorreu());
    }

}
