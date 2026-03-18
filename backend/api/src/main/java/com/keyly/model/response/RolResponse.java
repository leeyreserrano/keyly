package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Rol;
import com.keyly.model.response.basics.SucursalResponseBasic;

public record RolResponse(
        UUID uuid,
        String nom,
        SucursalResponseBasic sucursal) {

    public RolResponse(Rol r) {
        this(
                r.getUuid(),
                r.getNom(),
                new SucursalResponseBasic(r.getSucursal()));
    }

}
