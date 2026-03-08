package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
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

    public DepartamentResponse save(DepartamentRequest request) {
        Sucursal s = new Sucursal(sucursalService.getByUuid(request.sucursalUuid()));

        Departament departament = new Departament();

        departament.setSucursal(s);
        departament.setNom(request.nom());

        Departament departamentGuardat = repo.save(departament);

        return getByUuid(departamentGuardat.getUuid());
    }

    public DepartamentResponse update(UUID uuid, DepartamentRequest request) {
        Sucursal s = new Sucursal(sucursalService.getByUuid(request.sucursalUuid()));

        Departament departament = new Departament(s, getByUuid(uuid));
 
        return new DepartamentResponse(repo.save(departament));
    }

    public DepartamentResponse deleteByUuid(UUID uuid) {
        return new DepartamentResponse(repo.deleteByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Departament no trobat amb el uuid: " + uuid)));
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
        Sucursal s = new Sucursal(sucursalService.getByUuid(request.sucursalUuid()));

        Departament departament = new Departament(s, getById(id));

        Departament departamentGuardat = repo.save(departament);

        return new DepartamentResponse(departamentGuardat);
    }

    @Deprecated
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

}
