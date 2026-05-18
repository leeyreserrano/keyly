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
import com.keyly.model.response.BagulResponse;
import com.keyly.model.response.CarpetaResponse;
import com.keyly.model.response.EncryptedDataKeyResponse;
import com.keyly.model.response.ItemResponse;
import com.keyly.model.response.basics.CarpetaResponseBasic;
import com.keyly.repo.CarpetaRepo;
import com.keyly.repo.EncryptedDataKeysRepo;

@Service
public class CarpetaService {

    @Autowired
    private CarpetaRepo repo;

    @Autowired
    private EncryptedDataKeysRepo repoEncryptedDataKeys;

    @Autowired
    private BagulService bagulService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CompartitService compartitService;

    @Autowired
    private CarpetaMapper mapper;

    public List<CarpetaResponse> getAllCarpetes() {
        return repo.findAll()
                .stream()
                .map(this::toCarpetaResponse)
                .toList();
    }

    public List<CarpetaResponse> getAllCarpetesByUsuariUuid(UUID uuid) {
        return repo.findByBagulPropietariUuid(uuid)
                .stream()
                .map(this::toCarpetaResponse)
                .toList();
    }

    public CarpetaResponse getByUuid(UUID uuid) {
        Carpeta carpeta = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid: " + uuid));

        return toCarpetaResponse(carpeta);
    }

    public CarpetaResponse getUserCarpeta(Usuari usuari, UUID uuid) {
        Carpeta carpeta = getUserEntityCarpeta(usuari, uuid);

        return toCarpetaResponse(carpeta, usuari);
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
                .map(item -> new ItemResponse(item, repoEncryptedDataKeys.findAllByItemUuid(item.getUuid()),
                        foldersOfItem(item.getUuid())))
                .toList();
    }

    public List<ItemResponse> getUserCarpetaItem(Usuari usuari, UUID uuid) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + uuid));

        return carpeta.getItems()
                .stream()
                .map(item -> new ItemResponse(item,
                        repoEncryptedDataKeys.findByItemUuidAndUsuariUuid(uuid, usuari.getUuid()),
                        foldersOfItem(item.getUuid())))
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

        Carpeta carpetaGuardada = repo.save(carpeta);

        return toCarpetaResponse(carpetaGuardada);
    }

    public CarpetaResponse save(Usuari u, CarpetaRequest c) {
        Bagul b = bagulService.getBagulEntityByUsuariUuid(u.getUuid());

        Carpeta carpeta = new Carpeta(b, c);

        carpeta.setDataEditat(LocalDateTime.now());

        Carpeta carpetaGuardada = repo.save(carpeta);

        return toCarpetaResponse(carpetaGuardada);
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

        return toCarpetaResponse(carpeta, u);
    }

    public ItemResponse saveItemToUserCarpeta(Usuari u, UUID carpetaUuid, ItemRequest itemRequest) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(u, carpetaUuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + carpetaUuid));

        ItemResponse response = itemService.save(u, itemRequest);

        Item itemGuardat = itemService.getUserItemEntity(u, response.uuid());
        carpeta.addItem(itemGuardat);
        repo.save(carpeta);

        return new ItemResponse(itemGuardat, response.encryptedDataKey(), foldersOfItem(itemGuardat.getUuid()));
    }

    public CarpetaResponse update(UUID uuid, CarpetaRequest request) {
        Carpeta carpeta = getCarpetaEntityByUuid(uuid);

        if (request.bagulUuid() != null)
            carpeta.setBagul(bagulService.getBagulEntityByUuid(request.bagulUuid()));

        mapper.updateCarpetaFromDto(request, carpeta);

        carpeta.setDataEditat(LocalDateTime.now());

        Carpeta carpetaGuardada = repo.save(carpeta);

        return toCarpetaResponse(carpetaGuardada);
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

        return toCarpetaResponse(carpetaGuardada);
    }

    public CarpetaResponse deleteByUuid(UUID uuid) {
        CarpetaResponse carpeta = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        compartitService.getAllCompartits().stream()
                .filter(c -> c.uuid().equals(carpeta.uuid()))
                .forEach(c -> compartitService.deleteCompartit(c.uuid()));

        return carpeta;
    }

    public void deleteUserCarpeta(Usuari usuari, UUID uuid) {
        Carpeta carpeta = repo.findByBagulPropietariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el uuid " + uuid));

        compartitService.getAllCompartitsOfUser(usuari.getUuid()).stream()
                .filter(c -> c.uuid().equals(carpeta.getUuid()))
                .forEach(c -> compartitService.deleteCompartit(carpeta.getUuid()));

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

    public List<CarpetaResponseBasic> foldersOfItem(UUID itemUuid) {
        return toCarpetaResponseBasic(repo.findByItemsUuid(itemUuid));
    }

    public void registerAccess(Usuari usuari, UUID carpetaUuid) {
        Carpeta carpeta = getUserEntityCarpeta(usuari, carpetaUuid);

        carpeta.setUltimAccess(LocalDateTime.now());
        carpeta.setComptadorAccess(carpeta.getComptadorAccess() + 1);

        repo.save(carpeta);
    }

    public List<CarpetaResponseBasic> toCarpetaResponseBasic(List<Carpeta> carpeta) {
        return carpeta
                .stream()
                .map(c -> new CarpetaResponseBasic(c.getUuid(), c.getNom()))
                .toList();
    }

    public CarpetaResponse toCarpetaResponse(Carpeta carpeta, Usuari usuari) {
        List<ItemResponse> items = carpeta.getItems()
                .stream()
                .map(item -> {
                    EncryptedDataKeyResponse edk = repoEncryptedDataKeys
                            .findByItemUuidAndUsuariUuid(item.getUuid(), usuari.getUuid());
                    return new ItemResponse(item, edk, foldersOfItem(item.getUuid()));
                })
                .toList();

        return new CarpetaResponse(
                carpeta.getUuid(),
                new BagulResponse(carpeta.getBagul()),
                carpeta.getNom(),
                carpeta.getFavorit(),
                carpeta.getDataCreacio(),
                carpeta.getDataEditat(),
                carpeta.getUltimAccess(),
                carpeta.getComptadorAccess(),
                items);
    }

    public CarpetaResponse toCarpetaResponse(Carpeta carpeta) {
        return toCarpetaResponse(carpeta, carpeta.getBagul().getPropietari());
    }

}
