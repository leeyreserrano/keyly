package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.keyly.model.Carpeta;

public record CarpetaResponse(
        UUID uuid,
        BagulResponse bagul,
        List<ItemResponse> items,
        String nom,
        LocalDateTime dataCreacio) {

    public CarpetaResponse(Carpeta c) {
        this(
                c.getUuid(),
                new BagulResponse(c.getBagul()),
                c.getItems()
                        .stream()
                        .map(item -> new ItemResponse(item))
                        .toList(),
                c.getNom(),
                c.getDataCreacio());
    }

}
