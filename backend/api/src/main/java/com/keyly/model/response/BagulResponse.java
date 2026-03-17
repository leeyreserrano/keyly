package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Bagul;
import com.keyly.model.response.basics.UsuariResponseBasic;

public record BagulResponse(
        UUID uuid,
        UsuariResponseBasic usuari) {

    public BagulResponse(Bagul b) {
        this(
                b.getUuid(),
                new UsuariResponseBasic(b.getPropietari()));
    }

}
