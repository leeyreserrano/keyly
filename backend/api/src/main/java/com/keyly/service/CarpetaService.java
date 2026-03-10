package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.CarpetaMapper;
import com.keyly.model.Bagul;
import com.keyly.model.Carpeta;
import com.keyly.model.Item;
import com.keyly.model.request.CarpetaRequest;
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

    @Autowired
    private CarpetaMapper mapper;

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

    public Carpeta getCarpetaEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid: " + uuid));
    }

    public CarpetaResponse save(CarpetaRequest c) {
        Bagul b = bagulService.getBagulEntityByUuid(c.bagulUuid());

        Carpeta carpeta = new Carpeta(b, c);

        return new CarpetaResponse(repo.save(carpeta));
    }

    public CarpetaResponse saveItemToCarpeta(UUID carpetaUuid, UUID itemUuid) {
        Carpeta carpeta = getCarpetaEntityByUuid(carpetaUuid);
        Item itemRecuperat = itemService.getItemEntityByUuid(itemUuid);

        carpeta.addItem(itemRecuperat);
        repo.save(carpeta);

        return getById(carpeta.getId());
    }

    public CarpetaResponse update(UUID uuid, CarpetaRequest request) {
        Bagul b = bagulService.getBagulEntityByUuid(request.bagulUuid());

        Carpeta carpeta = getCarpetaEntityByUuid(uuid);

        carpeta.setBagul(b);

        mapper.updateCarpetaFromDto(request, carpeta);

        Carpeta carpetaGuardada = repo.save(carpeta);

        return new CarpetaResponse(carpetaGuardada);
    }

    public CarpetaResponse deleteByUuid(UUID uuid) {
        CarpetaResponse carpeta = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return carpeta;
    }

    public void deleteItemInCarpeta(UUID carpetaUuid, UUID itemUuid) {
        Carpeta carpeta = getCarpetaEntityByUuid(carpetaUuid);
        Item itemRecuperat = itemService.getItemEntityByUuid(itemUuid);
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
    public Carpeta getCarpetaEntityById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el id: " + id));
    }

    @Deprecated
    public CarpetaResponse update(Long id, CarpetaRequest request) {
        Bagul bagul = bagulService.getBagulEntityByUuid(request.bagulUuid());

        Carpeta carpeta = getCarpetaEntityById(id);

        carpeta.setBagul(bagul);

        mapper.updateCarpetaFromDto(request, carpeta);

        Carpeta carpetaGuardada = repo.save(carpeta);

        return new CarpetaResponse(carpetaGuardada);
    }

    @Deprecated
    public CarpetaResponse deleteById(Long id) {
        CarpetaResponse carpeta = getById(id);

        repo.deleteById(id);

        return carpeta;
    }

    @Deprecated
    public void deleteItemInCarpeta(Long carpetaId, Long itemId) {
        Carpeta carpeta = getCarpetaEntityById(carpetaId);
        Item itemRecuperat = itemService.getEntityById(itemId);
        carpeta.removeItem(itemRecuperat);
        repo.save(carpeta);
    }

}
