package com.keyly.model.response.basics;

import java.util.UUID;

import com.keyly.model.Item;
import com.keyly.model.response.ItemResponse;

public record ItemResponseBasic(
        UUID uuid,
        String titol,
        boolean dinsDeCarpeta) {
    public ItemResponseBasic(ItemResponse i) {
        this(
                i.uuid(),
                i.titol(),
                i.dinsDeCarpeta());
    }

    public ItemResponseBasic(Item i, boolean dinsDeCarpeta) {
        this(
                i.getUuid(),
                i.getTitol(),
                dinsDeCarpeta);
    }
}
