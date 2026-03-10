package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
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
    private UsuariService usuariService;

    @Autowired
    private CarpetaService carpetaService;

    @Autowired
    private ItemService itemService;

    public List<Compartit> getAllCompartits() {
        return repo.findAll();
    }

    public CompartitResponse getByUuid(UUID uuid) {
        Compartit c = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el uuid: " + uuid));

        if (c.getTipusEntitat() == TipusEntitat.CARPETA) {
            Carpeta carpeta = carpetaService.getCarpetaEntityByUuid(c.getUuid());

            return new CompartitResponse(c, new CarpetaResponse(carpeta));
        }

        Item item = itemService.getItemEntityByUuid(c.getEntitatUuid());

        return new CompartitResponse(c, new ItemResponse(item));
    }

    public Compartit getEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el uuid: " + uuid));
    }

    public CompartitResponse save(CompartitRequest c) {
        Usuari u = usuariService.getUsuariEntityByUuid(c.usuariUuid());

        Compartit compartit = new Compartit(u, c);

        Compartit compartitGuardat = repo.save(compartit);

        return getByUuid(compartitGuardat.getUuid());
    }

    public CompartitResponse update(UUID uuid, CompartitRequest request) {
        Compartit compartitGuardat = getEntityByUuid(uuid);

        Usuari u = usuariService.getUsuariEntityByUuid(request.usuariUuid());

        compartitGuardat.setUsuari(u);
        compartitGuardat.setTipusEntitat(request.tipusEntitat());
        compartitGuardat.setPermisos(request.permisos());
        if (request.tipusEntitat() == TipusEntitat.CARPETA) {
            Carpeta carpeta = carpetaService
                    .getCarpetaEntityByUuid(request.entitatUuid());
            compartitGuardat.setEntitatUuid(request.entitatUuid());
            return new CompartitResponse(repo.save(compartitGuardat), new CarpetaResponse(carpeta));
        }
        Item item = itemService.getItemEntityByUuid(request.entitatUuid());
        compartitGuardat.setEntitatUuid(item.getUuid());
        return new CompartitResponse(repo.save(compartitGuardat), new ItemResponse(item));
    }

    public void deleteByUuid(UUID uuid) {
        repo.deleteByUuid(uuid);
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public CompartitResponse getById(Long id) {
        Compartit c = repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el id: " + id));

        if (c.getTipusEntitat() == TipusEntitat.CARPETA) {
            Carpeta carpeta = carpetaService.getCarpetaEntityById(c.getId());

            return new CompartitResponse(c, new CarpetaResponse(carpeta));
        }

        Item item = itemService.getEntityById(c.getId());

        return new CompartitResponse(c, new ItemResponse(item));
    }

    @Deprecated
    public Compartit getEntityById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el id: " + id));
    }

    @Deprecated
    public CompartitResponse update(Long id, CompartitRequest request) {
        Compartit compartitGuardat = getEntityById(id);

        Usuari u = usuariService.getUsuariEntityByUuid(request.usuariUuid());

        compartitGuardat.setUsuari(u);
        compartitGuardat.setTipusEntitat(request.tipusEntitat());
        compartitGuardat.setPermisos(request.permisos());
        if (request.tipusEntitat() == TipusEntitat.CARPETA) {
            Carpeta carpeta = carpetaService
                    .getCarpetaEntityById(carpetaService.getCarpetaEntityByUuid(request.entitatUuid()).getId());
            compartitGuardat.setId(carpeta.getId());
            return new CompartitResponse(repo.save(compartitGuardat), new CarpetaResponse(carpeta));
        }
        Item item = itemService.getEntityById(itemService.getItemEntityByUuid(request.entitatUuid()).getId());
        compartitGuardat.setId(item.getId());
        return new CompartitResponse(repo.save(compartitGuardat), new ItemResponse(item));
    }

    @Deprecated
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

}
