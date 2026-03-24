package com.keyly.model.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public record ItemRequest(
        String titol,
        String nomUsuari,
        String contrasenya,
        String url,
        String notes,
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        Boolean favorit) {

}
