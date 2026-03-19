package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Departament;
import com.keyly.model.response.basics.SucursalResponseBasic;

public record DepartamentResponse(
        UUID uuid,
        String nom,
        SucursalResponseBasic sucursal) {

    public DepartamentResponse(Departament d) {
        this(
                d.getUuid(),
                d.getDepartament(),
                new SucursalResponseBasic(d.getSucursal()));
    }

}
