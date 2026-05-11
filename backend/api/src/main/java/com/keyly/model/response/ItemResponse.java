package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.keyly.model.Item;
import com.keyly.model.response.basics.BagulResponseBasic;
import com.keyly.model.response.basics.CarpetaResponseBasic;

public record ItemResponse(
        UUID uuid,
        String titol,
        String nomUsuari,
        String contrasenya,
        byte[] iv,
        EncryptedDataKeyResponse encryptedDataKey,
        String url,
        String notes,
        boolean favorit,
        LocalDateTime dataCreacio,
        LocalDateTime dataEditat,
        LocalDateTime ultimAccess,
        Long comptadorAccess,
        boolean dinsDeCarpeta,
        List<CarpetaResponseBasic> carpeta,
        BagulResponseBasic bagul) {

    public ItemResponse(Item i, EncryptedDataKeyResponse encryptedDataKey, List<CarpetaResponseBasic> carpeta) {
        this(
                i.getUuid(),
                i.getTitol(),
                i.getNomUsuari(),
                i.getContrasenya(),
                i.getIv(),
                encryptedDataKey,
                i.getUrl(),
                i.getNotes(),
                i.getFavorit(),
                i.getDataCreacio(),
                i.getDataEditat(),
                i.getDataUltimAcces(),
                i.getComptadorAccess(),
                carpeta != null && !carpeta.isEmpty(),
                carpeta,
                new BagulResponseBasic(i.getBagul(), i.getBagul().getPropietari()));
    }

}
