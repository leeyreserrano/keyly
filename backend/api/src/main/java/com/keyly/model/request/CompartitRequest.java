package com.keyly.model.request;

import java.util.List;
import java.util.UUID;

import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record CompartitRequest(
    List<UsuariPerCompartir> usuaris,
    TipusEntitat tipusEntitat,
    CarpetaRequest carpeta,
    ItemRequest item

) {

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UsuariPerCompartir {
    UUID uuid;
    Permisos permis;
}
