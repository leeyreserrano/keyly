package com.keyly.model.request;

import java.util.UUID;

public record DominiRequest(
    UUID sucursalUuid,
    String domini
) {

}
