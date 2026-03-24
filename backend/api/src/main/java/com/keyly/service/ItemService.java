package com.keyly.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.ItemMapper;
import com.keyly.model.Bagul;
import com.keyly.model.Carpeta;
import com.keyly.model.Item;
import com.keyly.model.Usuari;
import com.keyly.model.request.ItemRequest;
import com.keyly.model.response.ItemResponse;
import com.keyly.model.response.UsuariResponse;
import com.keyly.repo.ItemRepo;

@Service
public class ItemService {

    @Autowired
    private ItemRepo repo;

    @Autowired
    private BagulService bagulService;

    @Autowired
    private UsuariService usuariService;

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
                        carpetaService.hasItemInAnyCarpeta(item.getUuid())))
                .toList();
    }

    public List<ItemResponse> getAllItemsByUsuariUuid(UUID usuariUuid) {
        return repo.findAllByUsuariUuid(usuariUuid)
                .stream()
                .map(item -> new ItemResponse(item, carpetaService.hasItemInAnyCarpeta(item.getUuid())))
                .toList();
    }

    public ItemResponse getByUuid(UUID uuid) {
        Item item = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el uuid: " + uuid));

        return new ItemResponse(item, carpetaService.hasItemInAnyCarpeta(item.getUuid()));
    }

    public Item getItemEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el uuid: " + uuid));
    }

    public ItemResponse getUserItem(Usuari usuari, UUID uuid) {
        Item item = repo.findUserItemByUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el uuid: " + uuid));

        item.setDataUltimAcces(LocalDateTime.now());
        repo.save(item);

        return new ItemResponse(item, carpetaService.hasItemInAnyCarpeta(item.getUuid()));
    }

    public Item getUserItemEntity(Usuari usuari, UUID uuid) {
        return repo.findUserItemByUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el uuid: " + uuid));
    }

    public ItemResponse save(UsuariResponse u, ItemRequest i) {
        Bagul b = bagulService.getBagulEntityByUsuariUuid(u.uuid());

        Item item = new Item(b, i);

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        item.setIv(iv);

        if (item.getCarpetas() != null && !item.getCarpetas().isEmpty()) {
            Set<Carpeta> managed = new HashSet<>();

            for (Carpeta c : new HashSet<>(item.getCarpetas())) {
                Carpeta carpeta = carpetaService.getCarpetaEntityByUuid(c.getUuid());
                managed.add(carpeta);
            }
            item.setCarpetas(managed);

            for (Carpeta carpeta : managed) {
                carpeta.getItems().add(item);
            }
        }

        Item itemGuardat = repo.save(item);

        return new ItemResponse(itemGuardat, carpetaService.hasItemInAnyCarpeta(itemGuardat.getUuid()));
    }

    public ItemResponse update(UUID uuid, ItemRequest request) {
        Item item = getItemEntityByUuid(uuid);

        mapper.updateItemFromDto(request, item);

        Item itemGuardat = repo.save(item);

        return new ItemResponse(itemGuardat, carpetaService.hasItemInAnyCarpeta(itemGuardat.getUuid()));
    }

    public ItemResponse update(UsuariResponse usuari, UUID uuid, ItemRequest request) {
        Item item = repo.findUserItemByUuid(usuariService.getUsuariEntityByUuid(usuari.uuid()), uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException(
                        "Item amb uuid " + uuid + " no trobat per l'usuari amb el uuid " + usuari.uuid()));

        mapper.updateItemFromDto(request, item);

        item.setDataEditat(LocalDateTime.now());

        Item itemGuardat = repo.save(item);
        
        return new ItemResponse(itemGuardat, carpetaService.hasItemInAnyCarpeta(itemGuardat.getUuid()));
    }

    public ItemResponse deleteByUuid(UUID uuid) {
        ItemResponse item = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return item;
    }

    public ItemResponse deleteByUuid(Usuari usuari, UUID uuid) {
        Item item = repo.findUserItemByUuid(usuari, uuid).orElseThrow(() -> new EntitatNoTrobadaException("Item amb el uuid: " + uuid + " no trobat."));

        repo.deleteByUuid(item.getUuid());

        return new ItemResponse(item, false);
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public ItemResponse getById(Long id) {
        Item item = repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el id: " + id));

        return new ItemResponse(item, carpetaService.hasItemInAnyCarpeta(item.getUuid()));
    }

    @Deprecated
    public Item getEntityById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el id: " + id));
    }

    @Deprecated
    public ItemResponse deleteById(Long id) {
        ItemResponse item = getById(id);

        repo.deleteById(id);

        return item;
    }

}
