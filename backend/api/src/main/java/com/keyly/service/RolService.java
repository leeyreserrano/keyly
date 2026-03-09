package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.RolMapper;
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

    @Autowired
    private RolMapper mapper;

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

    public Rol getRolEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Rol no trobat amb el uuid: " + uuid));
    }

    public RolResponse save(RolRequest request) {
        Sucursal s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());

        Rol rol = new Rol();

        rol.setSucursal(s);
        rol.setNom(request.nom());

        return new RolResponse(repo.save(rol));
    }

    public RolResponse update(UUID uuid, RolRequest request) {
        Sucursal s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());

        Rol rol = getRolEntityByUuid(uuid);

        rol.setSucursal(s);

        mapper.updateRolFromDto(request, rol);

        return new RolResponse(repo.save(rol));
    }

    public RolResponse deleteByUuid(UUID uuid) {
        RolResponse rol = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return rol;
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
    public Rol getRolEntityById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Rol no trobat amb el id: " + id));

    }

    @Deprecated
    public RolResponse update(Long id, RolRequest request) {
        Sucursal s = null;

        if (request.sucursalUuid() != null)
            s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());

        Rol rol = getRolEntityById(id);

        if (s != null)
            rol.setSucursal(s);

        mapper.updateRolFromDto(request, rol);

        return new RolResponse(repo.save(rol));
    }

    @Deprecated
    public RolResponse deleteById(Long id) {
        RolResponse rol = getById(id);

        repo.deleteById(id);

        return rol;
    }

}
