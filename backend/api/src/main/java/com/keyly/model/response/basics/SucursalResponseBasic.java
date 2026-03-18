package com.keyly.model.response.basics;

import java.util.UUID;

import com.keyly.model.Sucursal;

public record SucursalResponseBasic(
        UUID uuid,
        String nom) {

    public SucursalResponseBasic(Sucursal s) {
        this(
                s.getUuid(),
                s.getNom());
    }

}
