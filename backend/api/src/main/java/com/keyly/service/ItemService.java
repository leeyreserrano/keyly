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
import com.keyly.mapper.ItemMapper;
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
    private ItemMapper mapper;

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

    public Item getItemEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el uuid: " + uuid));
    }

    public ItemResponse save(ItemRequest i) {
        Bagul b = bagulService.getBagulEntityByUuid(i.bagulUuid());

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

        return new ItemResponse(repo.save(item));
    }

    public ItemResponse update(UUID uuid, ItemRequest request) {
        Item item = getItemEntityByUuid(uuid);

        item.setBagul(bagulService.getBagulEntityByUuid(request.bagulUuid()));

        mapper.updateItemFromDto(request, item);

        return new ItemResponse(repo.save(item));
    }

    public ItemResponse deleteByUuid(UUID uuid) {
        ItemResponse item = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return item;
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
    public ItemResponse update(Long id, ItemRequest request) {
        Item item = getEntityById(id);

        item.setBagul(bagulService.getBagulEntityByUuid(request.bagulUuid()));

        mapper.updateItemFromDto(request, item);

        return new ItemResponse(repo.save(item));
    }

    @Deprecated
    public ItemResponse deleteById(Long id) {
        ItemResponse item = getById(id);

        repo.deleteById(id);

        return item;
    }

}
