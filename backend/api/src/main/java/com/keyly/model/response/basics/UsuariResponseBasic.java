package com.keyly.model.response.basics;

import java.util.UUID;

import com.keyly.model.Usuari;

public record UsuariResponseBasic(
        UUID uuid,
        String nom) {
    public UsuariResponseBasic(Usuari u) {
        this(
                u.getUuid(),
                u.getNom());
    }
}
