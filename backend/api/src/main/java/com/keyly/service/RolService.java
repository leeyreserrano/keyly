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
        Rol rol = getRolEntityByUuid(uuid);

        if (request.sucursalUuid() != null)
            rol.setSucursal(sucursalService.getSucursalEntityByUuid(request.sucursalUuid()));

        mapper.updateRolFromDto(request, rol);

        return new RolResponse(repo.save(rol));
    }

    public RolResponse deleteByUuid(UUID uuid) {
        RolResponse rol = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return rol;
    }

}
