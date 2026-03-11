package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.keyly.model.Item;

public record ItemResponse(
        UUID uuid,
        BagulResponse bagul,
        String titol,
        String nomUsuari,
        String contrasenya,
        String url,
        String notes,
        boolean favorit,
        LocalDateTime dataCreacio,
        LocalDateTime dataEditat,
        LocalDateTime ultimAccess) {

    public ItemResponse(Item i) {
        this(
                i.getUuid(),
                new BagulResponse(i.getBagul()),
                i.getTitol(),
                i.getNomUsuari(),
                i.getContrasenya(),
                i.getUrl(),
                i.getNotes(),
                i.getFavorit(),
                i.getDataCreacio(),
                i.getDataEditat(),
                i.getDataUltimAcces());
    }

}
