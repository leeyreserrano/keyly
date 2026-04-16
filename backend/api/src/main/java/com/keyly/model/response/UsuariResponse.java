package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.keyly.model.Usuari;
import com.keyly.model.enums.RolIntern;
import com.keyly.model.response.basics.DepartamentResponseBasic;
import com.keyly.model.response.basics.RolResponseBasic;
import com.keyly.model.response.basics.SucursalResponseBasic;

public record UsuariResponse(
        UUID uuid,
        String nom,
        String correu,
        byte[] kdfSalt,
        String imatge,
        LocalDateTime dataCreacio,
        LocalDateTime ultimLogin,
        boolean potAdministrar,
        RolIntern rolIntern,
        SucursalResponseBasic sucursal,
        DepartamentResponseBasic departament,
        RolResponseBasic rol) {

    public UsuariResponse(Usuari u) {
        this(
                u.getUuid(),
                u.getNom(),
                u.getCorreu(),
                u.getKdfSalt(),
                u.getImatge(),
                u.getDataCreacio(),
                u.getDataUltimLogin(),
                u.getPotAdministrar(),
                u.getRolIntern(),
                new SucursalResponseBasic(u.getSucursal()),
                new DepartamentResponseBasic(u.getDepartament()),
                new RolResponseBasic(u.getRol()));
    }

}
