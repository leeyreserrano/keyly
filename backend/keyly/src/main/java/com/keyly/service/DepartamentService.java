package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.model.Departament;
import com.keyly.model.Sucursal;
import com.keyly.repo.DepartamentRepo;
import com.keyly.repo.SucursalRepo;

@Service
public class DepartamentService {

    @Autowired
    private DepartamentRepo repo;

    @Autowired
    private SucursalRepo sucursalRepo;

    public List<Departament> getAllDepartaments() {
        return repo.findAll();
    }

    public Departament getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Departament save(Departament d) {
        Sucursal s = sucursalRepo.findById(d.getSucursal().getId()).orElse(null);

        if (s == null) {
            return null;
        }

        d.setSucursal(s);

        repo.save(d);

        return getById(d.getId());
    }

    public void delete(Departament d) {
        repo.deleteById(d.getId());
    }

}
