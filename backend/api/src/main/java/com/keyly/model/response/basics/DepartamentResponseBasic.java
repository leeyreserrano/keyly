package com.keyly.model.response.basics;

import java.util.UUID;

import com.keyly.model.Departament;

public record DepartamentResponseBasic(
        UUID uuid,
        String nom) {

    public DepartamentResponseBasic(Departament d) {
        this(
                d.getUuid(),
                d.getDepartament());
    }

}
