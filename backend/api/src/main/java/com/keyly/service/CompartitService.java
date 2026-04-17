package com.keyly.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Carpeta;
import com.keyly.model.Compartit;
import com.keyly.model.Item;
import com.keyly.model.Usuari;
import com.keyly.model.enums.TipusEntitat;
import com.keyly.model.request.CompartitRequest;
import com.keyly.model.response.CompartitResponse;
import com.keyly.model.response.basics.CarpetaResponseBasic;
import com.keyly.model.response.basics.ItemResponseBasic;
import com.keyly.model.response.basics.UsuariResponseBasic;
import com.keyly.repo.CompartitRepo;

@Service
public class CompartitService {

    @Autowired
    private CompartitRepo repo;

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

    public List<CompartitResponse> getAllCompartits() {
        return repo.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public CompartitResponse getCompartitByUuid(UUID uuid) {
        Compartit compartit = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb uuid: " + uuid));
        return convertToResponse(compartit);
    }

    public List<CompartitResponse> createCompartit(UUID creadorUuid, CompartitRequest request) {
        usuariService.getUsuariEntityByUuid(creadorUuid); // Validar que el creador existe
        List<CompartitResponse> responses = new ArrayList<>();

        if (request.tipusEntitat() == TipusEntitat.CARPETA) {
            carpetaService.getCarpetaEntityByUuid(request.entitatUuid());
        } else if (request.tipusEntitat() == TipusEntitat.ITEM) {
            itemService.getItemEntityByUuid(request.entitatUuid());
        }

        // Crea un compartit per a cada usuari
        for (UUID usuariUuid : request.usuarisUuid()) {
            if (usuariUuid.equals(creadorUuid)) {
                continue; // No el crea per l'usuari que ho crea
            }

            Usuari usuariReceptor = usuariService.getUsuariEntityByUuid(usuariUuid);

            Compartit compartit = new Compartit();
            compartit.setUsuari(usuariReceptor);
            compartit.setCreadorUuid(creadorUuid);
            compartit.setTipusEntitat(request.tipusEntitat());
            compartit.setEntitatUuid(request.entitatUuid());
            compartit.setPermisos(request.permisos());

            Compartit saved = repo.save(compartit);
            responses.add(convertToResponse(saved));
        }

        return responses;
    }

    public void deleteCompartit(UUID uuid) {
        Compartit compartit = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb uuid: " + uuid));
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
        UsuariResponseBasic creadorBasic = new UsuariResponseBasic(creador);

        CarpetaResponseBasic carpetaBasic = null;
        ItemResponseBasic itemBasic = null;

        if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
            try {
                Carpeta carpeta = carpetaService.getCarpetaEntityByUuid(compartit.getEntitatUuid());
                carpetaBasic = new CarpetaResponseBasic(carpeta);
            } catch (EntitatNoTrobadaException e) {
                
            }
        } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
            try {
                Item item = itemService.getItemEntityByUuid(compartit.getEntitatUuid());
                itemBasic = new ItemResponseBasic(item, false);
            } catch (EntitatNoTrobadaException e) {
                
            }
        }

        return new CompartitResponse(compartit, creadorBasic, carpetaBasic, itemBasic);
    }

}
