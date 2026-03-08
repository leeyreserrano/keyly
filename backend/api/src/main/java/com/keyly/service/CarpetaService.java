package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Bagul;
import com.keyly.model.Carpeta;
import com.keyly.model.Item;
import com.keyly.model.request.CarpetaRequest;
import com.keyly.model.request.ItemRequest;
import com.keyly.model.response.CarpetaResponse;
import com.keyly.model.response.ItemResponse;
import com.keyly.repo.CarpetaRepo;

@Service
public class CarpetaService {

    @Autowired
    private CarpetaRepo repo;

    @Autowired
    private BagulService bagulService;

    @Autowired
    private ItemService itemService;

    public List<CarpetaResponse> getAllCarpetes() {
        return repo.findAll()
                .stream()
                .map(carpeta -> new CarpetaResponse(carpeta))
                .toList();
    }

    public CarpetaResponse getByUuid(UUID uuid) {
        Carpeta carpeta = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid: " + uuid));

        return new CarpetaResponse(carpeta);
    }

    public List<ItemResponse> getCarpetaItem(UUID uuid) {
        CarpetaResponse carpeta = getByUuid(uuid);

        return carpeta.items();
    }

    public Carpeta getEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid: " + uuid));
    }

    public CarpetaResponse save(CarpetaRequest c) {
        Bagul b = bagulService.getEntityByUuid(c.bagulUuid());

        Carpeta carpeta = new Carpeta(b, c);

        Carpeta carpetaGuardada = repo.save(carpeta);

        return getByUuid(carpetaGuardada.getUuid());
    }

    public CarpetaResponse saveItemToCarpeta(UUID uuid, ItemRequest item) {
        Carpeta carpeta = getEntityByUuid(uuid);
        Item itemRecuperat = new Item(carpeta.getBagul(), item);

        carpeta.addItem(itemRecuperat);
        repo.save(carpeta);

        return getById(carpeta.getId());
    }

    public CarpetaResponse update(UUID uuid, CarpetaRequest request) {
        Carpeta carpetaGuardada = getEntityByUuid(uuid);

        carpetaGuardada.setBagul(bagulService.getEntityByUuid(request.bagulUuid()));
        carpetaGuardada.setNom(request.nom());

        return new CarpetaResponse(repo.save(carpetaGuardada));
    }

    public CarpetaResponse deleteByUuid(UUID uuid) {
        return new CarpetaResponse(repo.deleteByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid: " + uuid)));
    }

    public void deleteItemInCarpeta(UUID carpetaUuid, UUID itemUuid) {
        Carpeta carpeta = getEntityByUuid(carpetaUuid);
        Item itemRecuperat = itemService.getEntityByUuid(itemUuid);
        carpeta.removeItem(itemRecuperat);
        repo.save(carpeta);
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public CarpetaResponse getById(Long id) {
        return new CarpetaResponse(repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el id: " + id)));
    }

    @Deprecated
    public List<ItemResponse> getCarpetaItem(Long id) {
        CarpetaResponse carpeta = getById(id);

        return carpeta.items();
    }

    @Deprecated
    public Carpeta getEntityById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el id: " + id));
    }

    @Deprecated
    public CarpetaResponse saveItemToCarpeta(Long id, ItemRequest item) {
        Carpeta carpeta = getEntityById(id);
        Item itemRecuperat = new Item(carpeta.getBagul(), item);

        carpeta.addItem(itemRecuperat);
        repo.save(carpeta);

        return getById(carpeta.getId());
    }

    @Deprecated
    public CarpetaResponse update(Long id, CarpetaRequest request) {
        Carpeta carpetaGuardada = getEntityById(id);

        carpetaGuardada.setBagul(bagulService.getEntityByUuid(request.bagulUuid()));
        carpetaGuardada.setNom(request.nom());

        return new CarpetaResponse(repo.save(carpetaGuardada));
    }

    @Deprecated
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Deprecated
    public void deleteItemInCarpeta(Long carpetaId, Long itemId) {
        Carpeta carpeta = getEntityById(carpetaId);
        Item itemRecuperat = itemService.getEntityById(itemId);
        carpeta.removeItem(itemRecuperat);
        repo.save(carpeta);
    }

}
