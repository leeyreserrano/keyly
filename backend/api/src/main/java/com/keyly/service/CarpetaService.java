package com.keyly.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.CarpetaMapper;
import com.keyly.model.Bagul;
import com.keyly.model.Carpeta;
import com.keyly.model.Item;
import com.keyly.model.Usuari;
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

    @Autowired
    private CarpetaMapper mapper;

    public List<CarpetaResponse> getAllCarpetes() {
        return repo.findAll()
                .stream()
                .map(carpeta -> new CarpetaResponse(carpeta))
                .toList();
    }

    public List<CarpetaResponse> getAllCarpetesByUsuariUuid(UUID uuid) {
        return repo.findByBagulPropietariUuid(uuid)
                .stream()
                .map(carpeta -> new CarpetaResponse(carpeta))
                .toList();
    }

    public CarpetaResponse getByUuid(UUID uuid) {
        Carpeta carpeta = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid: " + uuid));

        return new CarpetaResponse(carpeta);
    }

    public CarpetaResponse getUserCarpeta(Usuari usuari, UUID uuid) {
        return new CarpetaResponse(getUserEntityCarpeta(usuari, uuid));
    }

    public Carpeta getUserEntityCarpeta(Usuari usuari, UUID carpetaUuid) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(usuari, carpetaUuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + carpetaUuid));

        return carpeta;
    }

    /**
     * Llistat dels items dins d'una carpeta per UUID.
     * 
     * @param uuid Identificador de la carpeta
     * @return Tots els items associats a la carpeta
     */
    public List<ItemResponse> getCarpetaItem(UUID uuid) {
        Carpeta carpeta = getCarpetaEntityByUuid(uuid);

        return carpeta.getItems()
                .stream()
                .map(item -> new ItemResponse(item, true))
                .toList();
    }

    public List<ItemResponse> getUserCarpetaItem(Usuari usuari, UUID uuid) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + uuid));

        return carpeta.getItems()
                .stream()
                .map(item -> new ItemResponse(item, true))
                .toList();
    }

    public Carpeta getCarpetaEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid: " + uuid));
    }

    public CarpetaResponse save(CarpetaRequest c) {
        Bagul b = bagulService.getBagulEntityByUuid(c.bagulUuid());

        Carpeta carpeta = new Carpeta(b, c);

        carpeta.setDataEditat(LocalDateTime.now());

        return new CarpetaResponse(repo.save(carpeta));
    }

    public CarpetaResponse save(Usuari u, CarpetaRequest c) {
        Bagul b = bagulService.getBagulEntityByUsuariUuid(u.getUuid());

        Carpeta carpeta = new Carpeta(b, c);

        carpeta.setDataEditat(LocalDateTime.now());

        return new CarpetaResponse(repo.save(carpeta));
    }

    public CarpetaResponse saveItemToCarpeta(UUID carpetaUuid, UUID itemUuid) {
        Carpeta carpeta = getCarpetaEntityByUuid(carpetaUuid);
        Item itemRecuperat = itemService.getItemEntityByUuid(itemUuid);

        carpeta.addItem(itemRecuperat);
        repo.save(carpeta);

        return getByUuid(carpeta.getUuid());
    }

    public CarpetaResponse saveItemToUserCarpeta(Usuari u, UUID carpetaUuid, UUID itemUuid) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(u, carpetaUuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + carpetaUuid));
        Item itemRecuperat = itemService.getUserItemEntity(u, itemUuid);

        carpeta.addItem(itemRecuperat);
        repo.save(carpeta);

        return new CarpetaResponse(carpeta);
    }

    public CarpetaResponse saveItemToUserCarpeta(Usuari u, UUID carpetaUuid, ItemRequest item) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(u, carpetaUuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + carpetaUuid));

        carpeta.addItem(new Item(bagulService.getBagulEntityByUsuariUuid(u.getUuid()), item));
        repo.save(carpeta);

        return new CarpetaResponse(carpeta);
    }

    public CarpetaResponse update(UUID uuid, CarpetaRequest request) {
        Carpeta carpeta = getCarpetaEntityByUuid(uuid);

        if (request.bagulUuid() != null)
            carpeta.setBagul(bagulService.getBagulEntityByUuid(request.bagulUuid()));

        mapper.updateCarpetaFromDto(request, carpeta);

        carpeta.setDataEditat(LocalDateTime.now());

        Carpeta carpetaGuardada = repo.save(carpeta);

        return new CarpetaResponse(carpetaGuardada);
    }

    public CarpetaResponse update(Usuari usuari, UUID uuid, CarpetaRequest request) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + uuid));

        if (request.bagulUuid() != null) {
            Bagul b = bagulService.getBagulEntityByUuid(request.bagulUuid());
            if (!b.getPropietari().equals(usuari)) {
                throw new EntitatNoTrobadaException("No autoritzat per canviar a aquest bagul");
            }
            carpeta.setBagul(b);
        }

        mapper.updateCarpetaFromDto(request, carpeta);

        carpeta.setDataEditat(LocalDateTime.now());

        Carpeta carpetaGuardada = repo.save(carpeta);

        return new CarpetaResponse(carpetaGuardada);
    }

    public CarpetaResponse deleteByUuid(UUID uuid) {
        CarpetaResponse carpeta = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return carpeta;
    }

    public void deleteUserCarpeta(Usuari usuari, UUID uuid) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + uuid));

        repo.delete(carpeta);
    }

    public void deleteItemInCarpeta(UUID carpetaUuid, UUID itemUuid) {
        Carpeta carpeta = getCarpetaEntityByUuid(carpetaUuid);
        Item itemRecuperat = itemService.getItemEntityByUuid(itemUuid);
        carpeta.removeItem(itemRecuperat);
        repo.save(carpeta);
    }

    public void deleteItemInUserCarpeta(Usuari u, UUID carpetaUuid, UUID itemUuid) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(u, carpetaUuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + carpetaUuid));
        Item itemRecuperat = itemService.getUserItemEntity(u, itemUuid);
        carpeta.removeItem(itemRecuperat);
        repo.save(carpeta);
    }

    public boolean hasItemInAnyCarpeta(UUID itemUuid) {
        return (repo.existItemInCarpetes(itemUuid) > 0) ? true : false;
    }

    public void registerAccess(Usuari usuari, UUID carpetaUuid) {
        Carpeta carpeta = getUserEntityCarpeta(usuari, carpetaUuid);

        carpeta.setUltimAccess(LocalDateTime.now());
        carpeta.setComptadorAccess(carpeta.getComptadorAccess() + 1);

        repo.save(carpeta);
    }

}
