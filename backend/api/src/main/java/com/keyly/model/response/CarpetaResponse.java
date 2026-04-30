package com.keyly.model.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.keyly.model.Carpeta;
import com.keyly.model.Item;
import com.keyly.model.Usuari;
import com.keyly.repo.EncryptedDataKeysRepo;

public record CarpetaResponse(
        UUID uuid,
        BagulResponse bagul,
        String nom,
        Boolean favorit,
        LocalDateTime dataCreacio,
        LocalDateTime dataEditat,
        LocalDateTime ultimAccess,
        Long comptadorAccess,
        List<ItemResponse> items) {

    public CarpetaResponse(Carpeta c, Usuari u) {
        this(
                c.getUuid(),
                new BagulResponse(c.getBagul()),
                c.getNom(),
                c.getFavorit(),
                c.getDataCreacio(),
                c.getDataEditat(),
                c.getUltimAccess(),
                c.getComptadorAccess(),
                c.getItems()
                        .stream()
                        .map(item -> new ItemResponse(item, searchEncryptedDataKeys(u, item),true))
                        .toList());
    }

    private static EncryptedDataKeyResponse searchEncryptedDataKeys(Usuari u, Item item) {
        EncryptedDataKeysRepo repo;

        return repo.findByItemUuidAndUsuariUuid(item.getUuid(), u.getUuid());
    }

}
