package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.SucursalMapper;
import com.keyly.model.Sucursal;
import com.keyly.model.request.SucursalRequest;
import com.keyly.model.response.SucursalResponse;
import com.keyly.repo.SucursalRepo;

@Service
public class SucursalService {

    @Autowired
    private SucursalRepo repo;

    @Autowired
    private SucursalMapper mapper;

    public List<SucursalResponse> getAllSucursals() {
        List<Sucursal> sucursals = repo.findAll();

        return sucursals
                .stream()
                .map(sucursal -> new SucursalResponse(sucursal))
                .toList();
    }

    private Sucursal getSucursalEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Sucursal no trobada amb el uuid: " + uuid));
    }

    public SucursalResponse getByUuid(UUID uuid) {
        Sucursal sucursal = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Sucursal no trobada amb el uuid: " + uuid));

        return new SucursalResponse(sucursal);
    }

    public SucursalResponse save(SucursalRequest sucursalRequest) {
        Sucursal sucursal = repo.save(new Sucursal(sucursalRequest));

        return getByUuid(sucursal.getUuid());
    }

    public SucursalResponse update(UUID uuid, SucursalRequest sucursalRequest) {
        Sucursal sucursalGuardada = getSucursalEntityByUuid(uuid);

        mapper.updateSucursalFromDto(sucursalRequest, sucursalGuardada);

        Sucursal sucursalGuardat = repo.save(sucursalGuardada);

        return new SucursalResponse(sucursalGuardat);
    }

    public SucursalResponse deleteByUuid(UUID uuid) {
        return new SucursalResponse(repo.deleteByUuid(uuid).orElseThrow(() -> new EntitatNoTrobadaException("Sucursal no trobada amb el uuid: " + uuid)));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public SucursalResponse getById(Long id) {
        Sucursal sucursal = repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Sucursal no trobada amb el id: " + id));

        return new SucursalResponse(sucursal);
    }

    @Deprecated
    public SucursalResponse update(Long id, SucursalRequest sucursalRequest) {
        Sucursal s = new Sucursal();

        s.setNom(sucursalRequest.nom());
        s.setDireccio(sucursalRequest.direccio());
        s.setCiutat(sucursalRequest.ciutat());
        s.setPais(sucursalRequest.pais());
        s.setTelefon(sucursalRequest.telefon());
        s.setCorreu(sucursalRequest.correu());

        Sucursal sucursalGuardat = repo.save(s);

        return new SucursalResponse(sucursalGuardat);
    }

    @Deprecated
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

}
