package com.keyly.model.request;

import java.util.Map;
import java.util.UUID;

import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;

public record CompartitRequest(
    UUID entitatUuid,
    TipusEntitat tipusEntitat,
    Map<UUID, Permisos> usuaris
) {
}
