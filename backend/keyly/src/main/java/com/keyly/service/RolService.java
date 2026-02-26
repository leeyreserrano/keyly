package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.repo.RolRepo;
import com.keyly.repo.SucursalRepo;

@Service
public class RolService {

    @Autowired
    private RolRepo repo;

    @Autowired
    private SucursalRepo sucursalRepo;

    public List<Rol> getAllRols() {
        return repo.findAll();
    }

    public Rol getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Rol save(Rol r) {
        Sucursal s = sucursalRepo.findById(r.getSucursal().getId()).orElse(null);

        if (s == null) {
            return null;
        }

        r.setSucursal(s);

        repo.save(r);

        return getById(r.getId());
    }

    public void delete(Rol r) {
        repo.delete(r);
    }

}
