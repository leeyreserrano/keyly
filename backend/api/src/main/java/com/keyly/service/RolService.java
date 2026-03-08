package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.request.RolRequest;
import com.keyly.model.response.RolResponse;
import com.keyly.repo.RolRepo;

@Service
public class RolService {

    @Autowired
    private RolRepo repo;

    @Autowired
    private SucursalService sucursalService;

    public List<RolResponse> getAllRols() {
        return repo.findAll()
                .stream()
                .map(rol -> new RolResponse(rol))
                .toList();
    }

    public RolResponse getByUuid(UUID uuid) {
        return new RolResponse(repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Rol no trobat amb el uuid: " + uuid)));
    }

    public RolResponse save(RolRequest request) {
        Sucursal s = new Sucursal(sucursalService.getByUuid(request.sucursalUuid()));

        Rol rol = new Rol();

        rol.setSucursal(s);

        repo.save(rol);

        return getByUuid(rol.getUuid());
    }

    public RolResponse update(UUID uuid, RolRequest request) {
        Sucursal s = new Sucursal(sucursalService.getByUuid(request.sucursalUuid()));

        Rol rol = new Rol(s, getByUuid(uuid));

        rol.setSucursal(s);
        rol.setNom(request.nom());

        return new RolResponse(repo.save(rol));
    }

    public RolResponse deleteByUuid(UUID uuid) {
        return new RolResponse(repo.deleteByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Rol no trobat amb el uuid: " + uuid)));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public RolResponse getById(Long id) {
        return new RolResponse(
                repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Rol no trobat amb el id: " + id)));
    }

    @Deprecated
    public RolResponse update(Long id, RolRequest request) {
        Sucursal s = new Sucursal(sucursalService.getByUuid(request.sucursalUuid()));

        Rol rol = new Rol(s, getById(id));

        rol.setSucursal(s);
        rol.setNom(request.nom());

        Rol rolGuardat = repo.save(rol);

        return new RolResponse(rolGuardat);
    }

    @Deprecated
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

}
