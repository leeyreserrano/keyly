package com.keyly.model.request;

public record SucursalRequest(
    String nom,
    String direccio,
    String ciutat,
    String pais,
    String telefon,
    String correu
) {

}
