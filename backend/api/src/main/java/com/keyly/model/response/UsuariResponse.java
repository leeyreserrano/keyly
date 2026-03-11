package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.keyly.model.Usuari;

public record UsuariResponse(
        UUID uuid,
        SucursalResponse sucursal,
        DepartamentResponse departament,
        RolResponse rol,
        String nom,
        String correu,
        String imatge,
        LocalDateTime dataCreacio,
        LocalDateTime ultimLogin,
        boolean potAdministrar) {

    public UsuariResponse(Usuari u) {
        this(
                u.getUuid(),
                new SucursalResponse(u.getSucursal()),
                new DepartamentResponse(u.getDepartament()),
                new RolResponse(u.getRol()),
                u.getNom(),
                u.getCorreu(),
                u.getImatge(),
                u.getDataCreacio(),
                u.getDataUltimLogin(),
                u.getPotAdministrar());
    }

}
