package com.keyly.model.response;

import java.util.UUID;

import com.keyly.model.Config;

public record ConfigResponse(
        UUID uuid,
        boolean permetreTotsDominis) {

    public ConfigResponse(Config c) {
        this(c.getUuid(), c.getPermetreTotsDominis());
    }

}
