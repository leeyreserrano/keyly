package com.keyly.model.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public record ConfigRequest(
        UUID sucursalUuid,
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        Boolean permetreTotsDominis) {

}
