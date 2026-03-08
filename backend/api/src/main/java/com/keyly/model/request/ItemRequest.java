package com.keyly.model.request;

import java.util.UUID;

public record ItemRequest(
        UUID bagulUuid,
        String titol,
        String nomUsuari,
        String contrasenya,
        String url,
        String notes,
        boolean favorit) {

}
