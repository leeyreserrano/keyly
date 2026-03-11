package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.keyly.model.Compartit;
import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;

public record CompartitResponse(
        UUID uuid,
        UsuariResponse usuari,
        TipusEntitat tipusEntitat,
        CarpetaResponse carpetaUuid,
        ItemResponse itemUuid,
        Permisos permisos,
        LocalDateTime dataCreacio) {

    public CompartitResponse(Compartit c, CarpetaResponse carpeta) {
        this(
                c.getUuid(),
                new UsuariResponse(c.getUsuari()),
                c.getTipusEntitat(),
                carpeta,
                null,
                c.getPermisos(),
                c.getDataCreacio());
    }

    public CompartitResponse(Compartit c, ItemResponse item) {
        this(
                c.getUuid(),
                new UsuariResponse(c.getUsuari()),
                c.getTipusEntitat(),
                null,
                item,
                c.getPermisos(),
                c.getDataCreacio());
    }

}
