package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.keyly.model.Carpeta;

public record CarpetaResponse(
        UUID uuid,
        BagulResponse bagul,
        String nom,
        Boolean favorit,
        LocalDateTime dataCreacio,
        List<ItemResponse> items) {

    public CarpetaResponse(Carpeta c) {
        this(
                c.getUuid(),
                new BagulResponse(c.getBagul()),
                c.getNom(),
                c.getFavorit(),
                c.getDataCreacio(),
                c.getItems()
                        .stream()
                        .map(item -> new ItemResponse(item, true))
                        .toList());
    }

}
