package com.keyly.service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Bagul;
import com.keyly.model.Carpeta;
import com.keyly.model.Item;
import com.keyly.model.request.ItemRequest;
import com.keyly.model.response.ItemResponse;
import com.keyly.repo.ItemRepo;

@Service
public class ItemService {

    @Autowired
    private ItemRepo repo;

    @Autowired
    private BagulService bagulService;

    @Autowired
    @Lazy
    private CarpetaService carpetaService;

    public List<ItemResponse> getAllItems() {
        return repo.findAll()
                .stream()
                .map(item -> new ItemResponse(item))
                .toList();
    }

    public ItemResponse getByUuid(UUID uuid) {
        Item item = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el uuid: " + uuid));

        return new ItemResponse(item);
    }

    public Item getEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el uuid: " + uuid));
    }

    public ItemResponse save(ItemRequest i) {
        Bagul b = bagulService.getEntityByUuid(i.bagulUuid());

        Item item = new Item(b, i);

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        item.setIv(iv);

        if (item.getCarpetas() != null && !item.getCarpetas().isEmpty()) {
            Set<Carpeta> managed = new HashSet<>();

            for (Carpeta c : new HashSet<>(item.getCarpetas())) {
                Carpeta carpeta = carpetaService.getEntityByUuid(c.getUuid());
                managed.add(carpeta);
            }
            item.setCarpetas(managed);

            for (Carpeta carpeta : managed) {
                carpeta.getItems().add(item);
            }
        }

        Item itemGuardat = repo.save(item);

        return getByUuid(itemGuardat.getUuid());
    }

    public ItemResponse update(UUID uuid, ItemRequest itemActualitzat) {
        Bagul b = bagulService.getEntityByUuid(itemActualitzat.bagulUuid());

        Item itemGuardat = getEntityByUuid(uuid);

        itemGuardat.setBagul(b);
        itemGuardat.setTitol(itemActualitzat.titol());
        itemGuardat.setContrasenya(itemActualitzat.contrasenya());
        itemGuardat.setUrl(itemActualitzat.url());
        itemGuardat.setNotes(itemActualitzat.notes());
        itemGuardat.setFavorit(itemActualitzat.favorit());

        repo.save(itemGuardat);

        return getById(itemGuardat.getId());
    }

    public ItemResponse deleteByUuid(UUID uuid) {
        return new ItemResponse(repo.deleteByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el uuid: " + uuid)));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public ItemResponse getById(Long id) {
        return new ItemResponse(
                repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el id: " + id)));
    }

    @Deprecated
    public Item getEntityById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el id: " + id));
    }

    @Deprecated
    public ItemResponse update(Long id, ItemRequest itemActualitzat) {
        Bagul b = bagulService.getEntityByUuid(itemActualitzat.bagulUuid());

        Item itemGuardat = getEntityById(id);

        itemGuardat.setBagul(b);
        itemGuardat.setTitol(itemActualitzat.titol());
        itemGuardat.setContrasenya(itemActualitzat.contrasenya());
        itemGuardat.setUrl(itemActualitzat.url());
        itemGuardat.setNotes(itemActualitzat.notes());
        itemGuardat.setFavorit(itemActualitzat.favorit());

        repo.save(itemGuardat);

        return getById(itemGuardat.getId());
    }

    @Deprecated
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

}
