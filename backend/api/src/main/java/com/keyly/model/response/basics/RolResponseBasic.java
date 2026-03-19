package com.keyly.model.response.basics;

import java.util.UUID;

import com.keyly.model.Rol;

public record RolResponseBasic(
        UUID uuid,
        String nom) {
    public RolResponseBasic(Rol r) {
        this(
                r.getUuid(),
                r.getNom());
    }
}
