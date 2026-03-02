package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Departament;
import com.keyly.model.Sucursal;
import com.keyly.repo.DepartamentRepo;

@Service
public class DepartamentService {

    @Autowired
    private DepartamentRepo repo;

    @Autowired
    private SucursalService sucursalService;

    public List<Departament> getAllDepartaments() {
        return repo.findAll();
    }

    public Departament getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Departament no trobat amb el id: " + id));
    }

    public Departament save(Departament d) {
        Sucursal s = sucursalService.getById(d.getSucursal().getId());

        d.setSucursal(s);

        repo.save(d);

        return getById(d.getId());
    }

    public void delete(Departament d) {
        Departament departament = getById(d.getId());

        repo.deleteById(departament.getId());
    }

}
