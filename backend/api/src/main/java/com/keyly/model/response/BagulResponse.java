package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Bagul;

public record BagulResponse(
        UUID uuid,
        UsuariResponse usuari) {

    public BagulResponse(Bagul b) {
        this(b.getUuid(), new UsuariResponse(b.getPropietari()));
    }

}
