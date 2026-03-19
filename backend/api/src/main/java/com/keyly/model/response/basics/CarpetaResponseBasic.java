package com.keyly.model.response.basics;

import java.util.UUID;

import com.keyly.model.response.CarpetaResponse;

public record CarpetaResponseBasic(
        UUID uuid,
        String nom) {
    public CarpetaResponseBasic(CarpetaResponse c) {
        this(
                c.uuid(),
                c.nom());
    }
}
