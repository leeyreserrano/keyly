package com.keyly.service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.CorreuExistentException;
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

    public DominiResponse save(DominiRequest d) {
        Sucursal s = sucursalService.getSucursalEntityByUuid(d.sucursalUuid());

        if (!esDominiValid(d.domini())) {
            throw new CorreuExistentException("El domini " + d.domini() + " no és un domini válid.");
        }

        Domini domini = new Domini();

        domini.setSucursal(s);
        domini.setDomini(d.domini());

        return new DominiResponse(repo.save(domini));
    }

    public DominiResponse update(UUID uuid, DominiRequest request) {
        Sucursal s = null;

        if (request.sucursalUuid() != null)
            s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());

        if (!esDominiValid(request.domini()) && request.domini() != null) {
            throw new CorreuExistentException("El domini " + request.domini() + " no és un domini válid.");
        }

        Domini domini = getDominiEntityByUuid(uuid);

        if (s != null) domini.setSucursal(s);

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

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public DominiResponse getById(Long id) {
        Domini domini = repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Domini no trobat amb el id: " + id));

        return new DominiResponse(domini);
    }

    @Deprecated
    public Domini getDominiEntityById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Domini no trobat amb el id: " + id));

    }

    @Deprecated
    public DominiResponse update(Long id, DominiRequest request) {
        Sucursal s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());

        if (!esDominiValid(request.domini()) && request.domini() != null) {
            throw new CorreuExistentException("El domini " + request.domini() + " no és un domini válid.");
        }

        Domini domini = getDominiEntityById(id);

        domini.setSucursal(s);

        mapper.updateDominiFromDto(request, domini);

        return new DominiResponse(repo.save(domini));
    }

    @Deprecated
    public DominiResponse deleteById(Long id) {
        DominiResponse domini = getById(id);

        repo.deleteById(id);

        return domini;
    }

}
