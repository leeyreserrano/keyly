package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Departament;

public record DepartamentResponse(
        UUID uuid,
        SucursalResponse sucursal,
        String nom) {

    public DepartamentResponse(Departament d) {
        this(
                d.getUuid(),
                new SucursalResponse(d.getSucursal()),
                d.getDomini());
    }

}
