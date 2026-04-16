package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;
import com.keyly.model.response.basics.CarpetaResponseBasic;
import com.keyly.model.response.basics.ItemResponseBasic;

public record CompartitResponse(
        UUID uuid,
        UsuariPerCompartirResponseBasic usuariCreador,
        List<UsuariPerCompartirResponseBasic> usuarisCompartits,
        TipusEntitat tipusEntitat,
        CarpetaResponseBasic carpeta,
        ItemResponseBasic item,
        LocalDateTime dataCreacio) {

}

record UsuariPerCompartirResponseBasic(
        UUID uuid,
        String nom,
        String correu,
        String imatge,
        Permisos permis) {

}
