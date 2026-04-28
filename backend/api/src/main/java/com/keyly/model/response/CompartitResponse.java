package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.keyly.model.Compartit;
import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;

public record CompartitResponse(
        UUID uuid,
        UsuariResponse usuariCreador,
        UsuariResponse usuariReceptor,
        TipusEntitat tipusEntitat,
        Permisos permisos,
        CarpetaResponse carpeta,
        ItemResponse item,
        LocalDateTime dataCreacio) {

        public CompartitResponse(Compartit c, UsuariResponse creador, CarpetaResponse carpeta, ItemResponse item) {
            this(
                c.getUuid(),
                creador,
                new UsuariResponse(c.getUsuari()),
                c.getTipusEntitat(),
                c.getPermisos(),
                carpeta,
                item,
                c.getDataCreacio()
            );
        }

}
