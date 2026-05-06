package com.keyly.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keyly.exception.CompartitException;
import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Carpeta;
import com.keyly.model.Compartit;
import com.keyly.model.EncryptedDataKeys;
import com.keyly.model.Item;
import com.keyly.model.Usuari;
import com.keyly.model.enums.Permisos;
import com.keyly.model.enums.TipusEntitat;
import com.keyly.model.request.CarpetaRequest;
import com.keyly.model.request.CompartitRequest;
import com.keyly.model.request.ItemRequest;
import com.keyly.model.request.UsuarisCompartits;
import com.keyly.model.request.combined.ItemEncryptedDataKey;
import com.keyly.model.response.CarpetaResponse;
import com.keyly.model.response.CompartitResponse;
import com.keyly.model.response.EncryptedDataKeyResponse;
import com.keyly.model.response.ItemResponse;
import com.keyly.model.response.UsuariResponse;
import com.keyly.repo.CompartitRepo;
import com.keyly.repo.EncryptedDataKeysRepo;

@Service
@Transactional
public class CompartitService {

    @Autowired
    private CompartitRepo repo;

    @Autowired
    private EncryptedDataKeysRepo repoEncryptedDataKeys;

    @Autowired
    private UsuariService usuariService;

    @Autowired
    @Lazy
    private CarpetaService carpetaService;

    @Autowired
    @Lazy
    private ItemService itemService;

    public List<CompartitResponse> getAllCompartitsOfUser(UUID usuariUuid) {
        return repo.findByUsuariUuid(usuariUuid)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<CompartitResponse> getAllCompartitsOfUserCreats(UUID usuariUuid) {
        return repo.findByCreadorUuid(usuariUuid)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<CompartitResponse> getAllCompartits() {
        return repo.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public CompartitResponse getCompartitByUuid(UUID uuid) {
        return convertToResponse(getCompartitEntityByUuid(uuid));
    }

    public Compartit getCompartitEntityByUuid(UUID uuid) {
        Compartit compartit = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb uuid: " + uuid));
        return compartit;
    }

    public List<CompartitResponse> createCompartit(UUID creadorUuid, CompartitRequest request) {
        usuariService.getUsuariEntityByUuid(creadorUuid);
        List<CompartitResponse> responses = new ArrayList<>();
    
        if (request.tipusEntitat() == TipusEntitat.CARPETA) {
            carpetaService.getCarpetaEntityByUuid(request.entitatUuid());
        } else if (request.tipusEntitat() == TipusEntitat.ITEM) {
            itemService.getItemEntityByUuid(request.entitatUuid());
        }
    
        for (UsuarisCompartits usuari : request.usuaris()) {
            UUID usuariUuid = usuari.usuariUuid();
            if (usuariUuid.equals(creadorUuid)) continue;
    
            Usuari usuariReceptor = usuariService.getUsuariEntityByUuid(usuariUuid);
    
            Compartit compartit = new Compartit();
            compartit.setUsuari(usuariReceptor);
            compartit.setCreadorUuid(creadorUuid);
            compartit.setTipusEntitat(request.tipusEntitat());
            compartit.setEntitatUuid(request.entitatUuid());
            compartit.setPermisos(usuari.permis());
    
            Compartit saved = repo.save(compartit);
            responses.add(convertToResponse(saved));
    
            if (request.tipusEntitat() == TipusEntitat.ITEM) {
                ItemEncryptedDataKey edk = usuari.encryptedDataKeys().get(0);
                Item item = itemService.getItemEntityByUuid(request.entitatUuid());
                EncryptedDataKeys e = new EncryptedDataKeys(null, null, item, usuariReceptor, edk.encryptedDataKey());
                repoEncryptedDataKeys.save(e);
    
            } else if (request.tipusEntitat() == TipusEntitat.CARPETA) {
                for (ItemEncryptedDataKey edk : usuari.encryptedDataKeys()) {
                    Item item = itemService.getItemEntityByUuid(edk.itemUuid());
                    EncryptedDataKeys e = new EncryptedDataKeys(null, null, item, usuariReceptor, edk.encryptedDataKey());
                    repoEncryptedDataKeys.save(e);
                }
            }
        }
    
        return responses;
    }

    public void createCompartit(UUID creadorUuid, ItemRequest item, CompartitRequest compartit) {
        ItemResponse response = itemService.save(usuariService.getUsuariEntityByUuid(creadorUuid), item);
        CompartitRequest request = new CompartitRequest(response.uuid(), TipusEntitat.ITEM, compartit.usuaris());
        createCompartit(creadorUuid, request);
    }

    public void createCompartit(UUID creadorUuid, CarpetaRequest carpeta, CompartitRequest compartit) {
        CarpetaResponse response = carpetaService.save(usuariService.getUsuariEntityByUuid(creadorUuid), carpeta);

        CompartitRequest request = new CompartitRequest(response.uuid(), TipusEntitat.ITEM, compartit.usuaris());

        createCompartit(creadorUuid, request);
    }

    public void deleteCompartit(UUID uuid) {
        Compartit compartit = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb uuid: " + uuid));

        repoEncryptedDataKeys.deleteByItemUuidAndUsuariUuid(compartit.getEntitatUuid(), compartit.getUsuari().getUuid());

        repo.delete(compartit);
    }

    public boolean hasAccess(UUID usuariUuid, UUID entitatUuid, TipusEntitat tipusEntitat) {
        return repo.findAll()
                .stream()
                .anyMatch(c -> c.getUsuari().getUuid().equals(usuariUuid) &&
                        c.getEntitatUuid().equals(entitatUuid) &&
                        c.getTipusEntitat() == tipusEntitat);
    }

    private CompartitResponse convertToResponse(Compartit compartit) {
        Usuari creador = usuariService.getUsuariEntityByUuid(compartit.getCreadorUuid());

        CarpetaResponse carpetaResponse = null;
        ItemResponse itemResponse = null;

        if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
            try {
                Carpeta carpeta = carpetaService.getCarpetaEntityByUuid(compartit.getEntitatUuid());
                carpetaResponse = carpetaService.toCarpetaResponse(carpeta);
            } catch (EntitatNoTrobadaException ignored) {
            }

        } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
            try {
                Item item = itemService.getItemEntityByUuid(compartit.getEntitatUuid());
                EncryptedDataKeyResponse edk = repoEncryptedDataKeys
                        .findByItemUuidAndUsuariUuid(item.getUuid(), compartit.getUsuari().getUuid());
                itemResponse = new ItemResponse(item, edk, false);
            } catch (EntitatNoTrobadaException ignored) {
            }
        }

        return new CompartitResponse(compartit, new UsuariResponse(creador), carpetaResponse, itemResponse);
    }

    public void updateCompartit(UUID creadorUuid, UUID compartitUuid, Permisos permisos) {
        Compartit compartit = getCompartitEntityByUuid(compartitUuid);

        if (compartit.getPermisos() != Permisos.ADMINISTRADOR)
            throw new CompartitException("L'usuari no és administrador.");

        compartit.setPermisos(permisos);
        repo.save(compartit);

    }

}
