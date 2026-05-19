package com.keyly.service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.DominiInvalidException;
import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.DominiMapper;
import com.keyly.model.Domini;
import com.keyly.model.Sucursal;
import com.keyly.model.request.DominiRequest;
import com.keyly.model.response.DominiResponse;
import com.keyly.repo.DominiRepo;

@Service
public class DominiService {

    @Autowired
    private DominiRepo repo;

    @Autowired
    private SucursalService sucursalService;

    @Autowired
    private DominiMapper mapper;

    public List<DominiResponse> getAllDominis() {
        return repo.findAll()
                .stream()
                .map(domini -> new DominiResponse(domini))
                .toList();
    }

    public DominiResponse getByUuid(UUID uuid) {
        Domini domini = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Domini no trobat amb el uuid: " + uuid));

        return new DominiResponse(domini);
    }

    public Domini getDominiEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Domini no trobat amb el uuid: " + uuid));
    }

    /**
     * Entrega tots els dominis que estiguin relacionats amb la sucursal indicada
     * 
     * @param uuid Identificador de la sucursal
     * @return Dominis de la sucursal
     */
    public List<DominiResponse> getDominisBySucursalUuid(UUID uuid) {
        List<Domini> dominis = repo.findBySucursalUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Sucursal no trobada amb el uuid: " + uuid));

        return dominis.stream().map(domini -> new DominiResponse(domini)).toList();
    }

    public DominiResponse save(DominiRequest d) {
        if (!esDominiValid(d.domini()))
            throw new DominiInvalidException("El domini " + d.domini() + " no és un domini válid.");

        Sucursal s = sucursalService.getSucursalEntityByUuid(d.sucursalUuid());

        if (repo.existsByDominiAndSucursalUuid(d.domini(), s.getUuid()))
            throw new DominiInvalidException("El domini " + d.domini() + " ja existeix.");

        Domini domini = new Domini();

        domini.setSucursal(s);
        domini.setDomini(d.domini());

        return new DominiResponse(repo.save(domini));
    }

    public DominiResponse update(UUID uuid, DominiRequest request) {
        if (!esDominiValid(request.domini()) && request.domini() != null) {
            throw new DominiInvalidException("El domini " + request.domini() + " no és un domini válid.");
        }

        if (repo.existsByDomini(request.domini()))
            throw new DominiInvalidException("El domini " + request.domini() + " ja existeix.");

        Domini domini = getDominiEntityByUuid(uuid);

        if (request.sucursalUuid() != null)
            domini.setSucursal(sucursalService.getSucursalEntityByUuid(request.sucursalUuid()));

        mapper.updateDominiFromDto(request, domini);

        return new DominiResponse(repo.save(domini));
    }

    public DominiResponse deleteByUuid(UUID uuid) {
        DominiResponse domini = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return domini;
    }

    public boolean esDominiValid(String domini) {
        final String EMAIL_REGEX = "^@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";

        return Pattern.matches(EMAIL_REGEX, domini);
    }

}
