package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Domini;
import com.keyly.model.response.basics.SucursalResponseBasic;

public record DominiResponse(
        UUID uuid,
        String domini,
        SucursalResponseBasic sucursal) {

    public DominiResponse(Domini d) {
        this(
                d.getUuid(),
                d.getDomini(),
                new SucursalResponseBasic(d.getSucursal()));
    }

}
