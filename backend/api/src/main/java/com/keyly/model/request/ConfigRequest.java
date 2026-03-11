package com.keyly.model.request;

import java.util.UUID;

public record ConfigRequest(
        UUID sucursalUuid,
        Boolean permetreTotsDominis) {

}
