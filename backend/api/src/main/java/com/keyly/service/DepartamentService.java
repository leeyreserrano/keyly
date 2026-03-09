package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.DepartamentMapper;
import com.keyly.model.Departament;
import com.keyly.model.Sucursal;
import com.keyly.model.request.DepartamentRequest;
import com.keyly.model.response.DepartamentResponse;
import com.keyly.repo.DepartamentRepo;

@Service
public class DepartamentService {

    @Autowired
    private DepartamentRepo repo;

    @Autowired
    private SucursalService sucursalService;

    @Autowired
    private DepartamentMapper mapper;

    public List<DepartamentResponse> getAllDepartaments() {
        return repo.findAll()
                .stream()
                .map(departament -> new DepartamentResponse(departament))
                .toList();
    }

    public DepartamentResponse getByUuid(UUID uuid) {
        Departament departament = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Departament no trobat amb el uuid: " + uuid));

        return new DepartamentResponse(departament);
    }

    public Departament getDepartamentEntityByUuid(UUID uuid) {
        Departament departament = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Departament no trobada amb el uuid: " + uuid));

        return departament;
    }

    public Departament getDepartamentEntityById(Long id) {
        Departament departament = repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Departament no trobada amb el id: " + id));

        return departament;
    }

    public DepartamentResponse save(DepartamentRequest request) {
        Sucursal s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());

        Departament departament = new Departament();

        departament.setSucursal(s);
        departament.setNom(request.nom());

        Departament departamentGuardat = repo.save(departament);

        return getByUuid(departamentGuardat.getUuid());
    }

    public DepartamentResponse update(UUID uuid, DepartamentRequest request) {
        Sucursal s = null;

        if (request.sucursalUuid() != null)
            s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());

        Departament d = getDepartamentEntityByUuid(uuid);

        if (s != null)
            d.setSucursal(s);

        mapper.updateDepartamentFromDto(request, d);

        return new DepartamentResponse(repo.save(d));
    }

    public DepartamentResponse deleteByUuid(UUID uuid) {
        DepartamentResponse departament = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return departament;
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public DepartamentResponse getById(Long id) {
        Departament departament = repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Departament no trobat amb el id: " + id));

        return new DepartamentResponse(departament);
    }

    @Deprecated
    public DepartamentResponse update(Long id, DepartamentRequest request) {
        Sucursal s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());

        Departament d = getDepartamentEntityById(id);

        if (s != null)
            d.setSucursal(s);

        mapper.updateDepartamentFromDto(request, d);

        return new DepartamentResponse(repo.save(d));
    }

    @Deprecated
    public DepartamentResponse deleteById(Long id) {
        DepartamentResponse departament = getById(id);

        repo.deleteById(id);

        return departament;
    }

}
