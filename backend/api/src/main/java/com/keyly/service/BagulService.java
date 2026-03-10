package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.BagulMapper;
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

    @Autowired
    private BagulMapper mapper;

    public List<BagulResponse> getAllBaguls() {
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

    public Bagul getBagulEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Bagul no trobat amb el uuid: " + uuid));
    }

    public BagulResponse save(BagulRequest b) {
        Usuari usuari = usuariService.getUsuariEntityByUuid(b.propietariUuid());

        Bagul bagul = new Bagul(usuari);

        return new BagulResponse(repo.save(bagul));
    }

    public BagulResponse update(UUID uuid, BagulRequest request) {
        Bagul bagul = getBagulEntityByUuid(uuid);

        bagul.setPropietari(usuariService.getUsuariEntityByUuid(request.propietariUuid()));

        mapper.updateBagulFromDto(request, bagul);

        return new BagulResponse(repo.save(bagul));
    }

    public BagulResponse deleteByUuid(UUID uuid) {
        BagulResponse bagul = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return bagul;
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
    public Bagul getBagulEntityById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Bagul no trobat amb el id: " + id));
    }

    @Deprecated
    public BagulResponse update(Long id, BagulRequest request) {
        Bagul bagul = getBagulEntityById(id);

        bagul.setPropietari(usuariService.getUsuariEntityByUuid(request.propietariUuid()));

        mapper.updateBagulFromDto(request, bagul);

        return new BagulResponse(repo.save(bagul));
    }

    @Deprecated
    public BagulResponse deleteById(Long id) {
        BagulResponse bagul = getById(id);

        repo.deleteById(id);

        return bagul;
    }

}
