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
        UsuariResponseBasic usuariCreador,
        UsuariResponseBasic usuariReceptor,
        TipusEntitat tipusEntitat,
        Permisos permisos,
        CarpetaResponseBasic carpeta,
        ItemResponseBasic item,
        LocalDateTime dataCreacio) {

        public CompartitResponse(Compartit c, UsuariResponseBasic creador, CarpetaResponseBasic carpeta, ItemResponseBasic item) {
            this(
                c.getUuid(),
                creador,
                new UsuariResponseBasic(c.getUsuari()),
                c.getTipusEntitat(),
                c.getPermisos(),
                carpeta,
                item,
                c.getDataCreacio()
            );
        }

}
