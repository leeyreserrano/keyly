package com.keyly.model.request;

import java.util.UUID;

public record DepartamentRequest(
        UUID sucursalUuid,
        String nom) {
}
