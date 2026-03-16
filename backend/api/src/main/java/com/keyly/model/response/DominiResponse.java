package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Domini;

public record DominiResponse(
        UUID uuid,
        SucursalResponse sucursal,
        String domini) {

    public DominiResponse(Domini d) {
        this(
                d.getUuid(),
                new SucursalResponse(d.getSucursal()),
                d.getDomini());
    }
    
}
