package com.keyly.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.ItemMapper;
import com.keyly.model.Bagul;
import com.keyly.model.EncryptedDataKeys;
import com.keyly.model.Item;
import com.keyly.model.Usuari;
import com.keyly.model.request.ItemRequest;
import com.keyly.model.response.EncryptedDataKeyResponse;
import com.keyly.model.response.ItemResponse;
import com.keyly.model.response.UsuariResponse;
import com.keyly.repo.EncryptedDataKeysRepo;
import com.keyly.repo.ItemRepo;

@Service
public class ItemService {

    @Autowired
    private ItemRepo repo;

    @Autowired
    private EncryptedDataKeysRepo repoEncryptedDataKeys;

    @Autowired
    private BagulService bagulService;

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private CompartitService compartitService;

    @Autowired
    private ItemMapper mapper;

    @Autowired
    @Lazy
    private CarpetaService carpetaService;

    public List<ItemResponse> getAllItems() {
        return repo.findAll()
                .stream()
                .map(item -> new ItemResponse(
                        item,
                        repoEncryptedDataKeys.findAllByItemUuid(item.getUuid()),
                        carpetaService.foldersOfItem(item.getUuid())))
                .toList();
    }

    public List<ItemResponse> getAllItemsByUsuariUuid(UUID usuariUuid) {
        return repo.findByBagulPropietariUuid(usuariUuid)
                .stream()
                .map(item -> new ItemResponse(item,
                        repoEncryptedDataKeys.findByItemUuidAndUsuariUuid(item.getUuid(),
                                usuariUuid),
                        carpetaService.foldersOfItem(item.getUuid())))
                .toList();
    }

    public ItemResponse getByUuid(UUID uuid) {
        Item item = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException(
                        "Item no trobat amb el uuid: " + uuid));

        return new ItemResponse(item, repoEncryptedDataKeys.findAllByItemUuid(uuid),
                carpetaService.foldersOfItem(item.getUuid()));
    }

    public Item getItemEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException(
                        "Item no trobat amb el uuid: " + uuid));
    }

    public ItemResponse getUserItem(Usuari usuari, UUID uuid) {
        Item item = repo.findByBagulPropietariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException(
                        "Item no trobat amb el uuid: " + uuid));

        item.setDataUltimAcces(LocalDateTime.now());
        repo.save(item);

        return new ItemResponse(item, repoEncryptedDataKeys.findByItemUuidAndUsuariUuid(uuid, usuari.getUuid()),
                carpetaService.foldersOfItem(item.getUuid()));
    }

    public Item getUserItemEntity(Usuari usuari, UUID uuid) {
        return repo.findByBagulPropietariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException(
                        "Item no trobat amb el uuid: " + uuid));
    }

    public ItemResponse save(Usuari u, ItemRequest i) {
        Bagul b = bagulService.getBagulEntityByUsuariUuid(u.getUuid());

        Item item = new Item(b, i);
        Item itemGuardat = repo.save(item);

        EncryptedDataKeys e = new EncryptedDataKeys(
                null, null,
                itemGuardat,
                u,
                i.encryptedDataKey());

        EncryptedDataKeys encryptedDataKeyGuardat = repoEncryptedDataKeys.save(e);

        return new ItemResponse(
                itemGuardat,
                new EncryptedDataKeyResponse(encryptedDataKeyGuardat),
                null);
    }

    public ItemResponse update(UsuariResponse usuari, UUID uuid, ItemRequest request) {
        Item item = repo.findByBagulPropietariAndUuid(usuariService.getUsuariEntityByUuid(usuari.uuid()), uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException(
                        "Item amb uuid " + uuid + " no trobat per l'usuari amb el uuid "
                                + usuari.uuid()));

        mapper.updateItemFromDto(request, item);

        item.setDataEditat(LocalDateTime.now());

        Item itemGuardat = repo.save(item);

        EncryptedDataKeys e = repoEncryptedDataKeys.findByUsuariUuidAndItemUuid(usuari.uuid(), item.getUuid());

        e.setEncryptedDataKey(request.encryptedDataKey());

        EncryptedDataKeys encryptedDataKeyGuardat = repoEncryptedDataKeys.save(e);

        return new ItemResponse(itemGuardat, new EncryptedDataKeyResponse(encryptedDataKeyGuardat),
                carpetaService.foldersOfItem(itemGuardat.getUuid()));
    }

    public ItemResponse deleteByUuid(UUID uuid) {
        ItemResponse item = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        compartitService.getAllCompartits().stream()
                .filter(c -> item.uuid().equals(c.item().uuid()))
                .forEach(c -> compartitService.deleteCompartit(c.uuid()));

        return item;
    }

    public void deleteByUuid(Usuari usuari, UUID uuid) {
        Item item = repo.findByBagulPropietariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException(
                        "Item amb el uuid: " + uuid + " no trobat."));

        compartitService.getAllCompartitsOfUser(usuari.getUuid())
                .stream()
                .filter(c -> item.getUuid().equals(c.item().uuid()))
                .forEach(c -> compartitService.deleteCompartit(c.uuid()));

        repo.deleteByUuid(item.getUuid());
    }

    public void registerAccess(UUID userUuid, UUID itemUuid) {
        Usuari usuari = usuariService.getUsuariEntityByUuid(userUuid);

        Item item = getUserItemEntity(usuari, itemUuid);

        item.setDataUltimAcces(LocalDateTime.now());

        item.setComptadorAccess(item.getComptadorAccess() + 1);

        repo.save(item);
    }

}
