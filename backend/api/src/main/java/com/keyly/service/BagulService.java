package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.BagulMapper;
import com.keyly.model.Bagul;
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

    public BagulResponse update(UUID uuid, BagulRequest request) {
        Bagul bagul = getBagulEntityByUuid(uuid);

        if (request.propietariUuid() != null)
            bagul.setPropietari(usuariService.getUsuariEntityByUuid(request.propietariUuid()));

        mapper.updateBagulFromDto(request, bagul);

        return new BagulResponse(repo.save(bagul));
    }

}
