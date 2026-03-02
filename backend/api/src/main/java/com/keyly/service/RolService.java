package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.repo.RolRepo;

@Service
public class RolService {

    @Autowired
    private RolRepo repo;

    @Autowired
    private SucursalService sucursalService;

    public List<Rol> getAllRols() {
        return repo.findAll();
    }

    public Rol getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Rol no trobat amb el id: " + id));
    }

    public Rol save(Rol r) {
        Sucursal s = sucursalService.getById(r.getSucursal().getId());

        r.setSucursal(s);

        repo.save(r);

        return getById(r.getId());
    }

    public void delete(Rol r) {
        Rol rol = getById(r.getId());

        repo.deleteById(rol.getId());
    }

}
