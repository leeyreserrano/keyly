package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Bagul;
import com.keyly.model.Usuari;
import com.keyly.model.request.BagulRequest;
import com.keyly.model.response.BagulResponse;
import com.keyly.repo.BagulRepo;

@Service
public class BagulService {

    @Autowired
    private BagulRepo repo;

    @Autowired
    private UsuariService usuariService;

    public List<BagulResponse> getAllContrasenyes() {
        return repo.findAll()
                .stream()
                .map(bagul -> new BagulResponse(bagul))
                .toList();
    }

    public BagulResponse getByUuid(UUID uuid) {
        Bagul bagul = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Bagul no trobat amb el uuid: " + uuid));

        return new BagulResponse(bagul);
    }

    public Bagul getEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Bagul no trobat amb el uuid: " + uuid));
    }

    public BagulResponse save(BagulRequest b) {
        Usuari usuari = usuariService.getEntityByUuid(b.propietariUuid());

        Bagul bagul = new Bagul(usuari);

        Bagul bagulGuardat = repo.save(bagul);

        return getByUuid(bagulGuardat.getUuid());
    }

    public BagulResponse update(UUID uuid, BagulRequest request) {
        Bagul bagul = getEntityByUuid(uuid);

        bagul.setPropietari(usuariService.getEntityByUuid(request.propietariUuid()));

        return new BagulResponse(repo.save(bagul));
    }

    public BagulResponse deleteByUuid(UUID uuid) {
        return new BagulResponse(repo.deleteByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Bagul no trobat amb el uuid: " + uuid)));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public BagulResponse getById(Long id) {
        Bagul bagul = repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Bagul no trobat amb el id: " + id));

        return new BagulResponse(bagul);
    }

    @Deprecated
    public Bagul getEntityById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Bagul no trobat amb el id: " + id));
    }

    @Deprecated
    public BagulResponse update(Long id, BagulRequest request) {
        Bagul bagul = getEntityById(id);

        bagul.setPropietari(usuariService.getEntityByUuid(request.propietariUuid()));

        return new BagulResponse(repo.save(bagul));
    }

    @Deprecated
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

}
