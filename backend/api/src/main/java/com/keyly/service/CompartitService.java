package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.CompartitMapper;
import com.keyly.model.Carpeta;
import com.keyly.model.Compartit;
import com.keyly.model.Item;
import com.keyly.model.Usuari;
import com.keyly.model.enums.TipusEntitat;
import com.keyly.model.request.CompartitRequest;
import com.keyly.model.response.CarpetaResponse;
import com.keyly.model.response.CompartitResponse;
import com.keyly.model.response.ItemResponse;
import com.keyly.repo.CompartitRepo;

@Service
public class CompartitService {

    @Autowired
    private CompartitRepo repo;

    @Autowired
    private CarpetaService carpetaService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CompartitMapper mapper;

    public List<CompartitResponse> getAllCompartits() {
        return repo.findAll()
                .stream()
                .map(item -> {
                    if (item.getTipusEntitat() == TipusEntitat.CARPETA)
                        return new CompartitResponse(item, carpetaService.getByUuid(item.getEntitatUuid()));

                    return new CompartitResponse(item, itemService.getByUuid(item.getEntitatUuid()));
                })
                .toList();
    }

    public List<CompartitResponse> getAllCompartitsByUsuariUuid(UUID uuid) {
        return repo.findByUsuariUuid(uuid)
                .stream()
                .map(item -> {
                    if (item.getTipusEntitat() == TipusEntitat.CARPETA)
                        return new CompartitResponse(item, carpetaService.getByUuid(item.getEntitatUuid()));

                    return new CompartitResponse(item, itemService.getByUuid(item.getEntitatUuid()));
                })
                .toList();
    }

    public CompartitResponse getByUuid(UUID uuid) {
        Compartit c = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el uuid: " + uuid));

        if (c.getTipusEntitat() == TipusEntitat.CARPETA) {
            Carpeta carpeta = carpetaService.getCarpetaEntityByUuid(c.getUuid());

            return new CompartitResponse(c, new CarpetaResponse(carpeta));
        }

        Item item = itemService.getItemEntityByUuid(c.getEntitatUuid());

        return new CompartitResponse(c, new ItemResponse(item, carpetaService.hasItemInAnyCarpeta(item.getUuid())));
    }

    public Compartit getEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el uuid: " + uuid));
    }

    public CompartitResponse getUserCompartit(Usuari usuari, UUID uuid) {
        Compartit compartit = repo.findByUsuariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el uuid: " + uuid));

        if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
            Carpeta carpeta = carpetaService.getCarpetaEntityByUuid(compartit.getEntitatUuid());

            return new CompartitResponse(compartit, new CarpetaResponse(carpeta));
        }

        Item item = itemService.getItemEntityByUuid(compartit.getEntitatUuid());

        return new CompartitResponse(compartit,
                new ItemResponse(item, carpetaService.hasItemInAnyCarpeta(item.getUuid())));
    }

    public CompartitResponse save(Usuari u, CompartitRequest c) {
        Compartit compartit = new Compartit(u, c);

        Compartit compartitGuardat = repo.save(compartit);

        return getByUuid(compartitGuardat.getUuid());
    }

    public CompartitResponse update(UUID uuid, CompartitRequest request) {
        Compartit compartitGuardat = getEntityByUuid(uuid);

        if (request.tipusEntitat() != null)
            compartitGuardat.setTipusEntitat(request.tipusEntitat());

        if (request.permisos() != null)
            compartitGuardat.setPermisos(request.permisos());

        if (compartitGuardat.getTipusEntitat() == TipusEntitat.CARPETA) {
            Carpeta carpeta = carpetaService
                    .getCarpetaEntityByUuid(request.entitatUuid());
            compartitGuardat.setEntitatUuid(request.entitatUuid());
            return new CompartitResponse(repo.save(compartitGuardat), new CarpetaResponse(carpeta));
        }
        Item item = itemService.getItemEntityByUuid(request.entitatUuid());
        compartitGuardat.setEntitatUuid(item.getUuid());

        return new CompartitResponse(repo.save(compartitGuardat),
                new ItemResponse(item, carpetaService.hasItemInAnyCarpeta(item.getUuid())));
    }

    public CompartitResponse update(Usuari usuari, UUID uuid, CompartitRequest request) {
        Compartit compartit = repo.findByUsuariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el uuid " + uuid));

        mapper.updateCompartitFromDto(request, compartit);

        Compartit compartitGuardat = repo.save(compartit);

        if (compartitGuardat.getTipusEntitat() == TipusEntitat.CARPETA) {
            Carpeta carpeta = carpetaService.getCarpetaEntityByUuid(compartitGuardat.getEntitatUuid());

            return new CompartitResponse(compartitGuardat, new CarpetaResponse(carpeta));
        }

        Item item = itemService.getItemEntityByUuid(compartitGuardat.getEntitatUuid());

        return new CompartitResponse(compartitGuardat,
                new ItemResponse(item, carpetaService.hasItemInAnyCarpeta(item.getUuid())));
    }

    public void deleteByUuid(UUID uuid) {
        repo.deleteByUuid(uuid);
    }

    public void deleteUserCompartit(Usuari usuari, UUID uuid) {
        Compartit compartit = repo.findByUsuariAndUuid(usuari, uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el uuid " + uuid));

        repo.delete(compartit);
    }

}
