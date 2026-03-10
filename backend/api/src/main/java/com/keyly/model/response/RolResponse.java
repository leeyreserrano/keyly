package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Rol;

public record RolResponse(
        UUID uuid,
        SucursalResponse sucursal,
        String nom) {

    public RolResponse(Rol r) {
        this(r.getUuid(), new SucursalResponse(r.getSucursal()), r.getNom());
    }

}
