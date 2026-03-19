package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.keyly.model.Compartit;
import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;
import com.keyly.model.response.basics.CarpetaResponseBasic;
import com.keyly.model.response.basics.ItemResponseBasic;
import com.keyly.model.response.basics.UsuariResponseBasic;

public record CompartitResponse(
        UUID uuid,
        UsuariResponseBasic usuari,
        TipusEntitat tipusEntitat,
        CarpetaResponseBasic carpeta,
        ItemResponseBasic item,
        Permisos permisos,
        LocalDateTime dataCreacio) {

    public CompartitResponse(Compartit c, CarpetaResponse carpeta) {
        this(
                c.getUuid(),
                new UsuariResponseBasic(c.getUsuari()),
                c.getTipusEntitat(),
                new CarpetaResponseBasic(carpeta),
                null,
                c.getPermisos(),
                c.getDataCreacio());
    }

    public CompartitResponse(Compartit c, ItemResponse item) {
        this(
                c.getUuid(),
                new UsuariResponseBasic(c.getUsuari()),
                c.getTipusEntitat(),
                null,
                new ItemResponseBasic(item),
                c.getPermisos(),
                c.getDataCreacio());
    }

}
