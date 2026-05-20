package com.keyly.model.request;

import java.util.List;
import java.util.UUID;

import com.keyly.model.enums.TipusEntitat;

public record CompartitRequest(
    UUID entitatUuid,
    TipusEntitat tipusEntitat,
    List<UsuarisCompartits> usuaris
) {
}