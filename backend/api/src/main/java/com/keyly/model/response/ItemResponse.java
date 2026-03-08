package com.keyly.model.response;

import java.sql.Date;
import java.util.UUID;

import com.keyly.model.Item;

public record ItemResponse(
        UUID uuid,
        BagulResponse bagulResponse,
        String titol,
        String nomUsuari,
        String contrasenya,
        String url,
        String notes,
        boolean favorit,
        Date dataCreacio,
        Date dataEditat,
        Date ultimAccess) {

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
