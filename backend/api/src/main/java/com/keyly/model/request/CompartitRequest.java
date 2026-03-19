package com.keyly.model.request;

import java.util.UUID;

import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;

public record CompartitRequest(
    UUID usuariUuid,
    TipusEntitat tipusEntitat,
    UUID entitatUuid,
    Permisos permisos

) {

}
