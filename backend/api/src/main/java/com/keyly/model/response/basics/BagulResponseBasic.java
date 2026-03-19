package com.keyly.model.response.basics;

import java.util.UUID;

import com.keyly.model.Bagul;
import com.keyly.model.Usuari;

public record BagulResponseBasic(
        UUID uuid,
        UsuariResponseBasic usuari) {
    public BagulResponseBasic(Bagul b, Usuari u) {
        this(
                b.getUuid(),
                new UsuariResponseBasic(u));
    }
}
