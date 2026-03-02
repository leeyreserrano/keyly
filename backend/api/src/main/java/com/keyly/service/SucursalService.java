package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Sucursal;
import com.keyly.repo.SucursalRepo;

@Service
public class SucursalService {

    @Autowired
    private SucursalRepo repo;

    public List<Sucursal> getAllSucursals() {
        return repo.findAll();
    }

    public Sucursal getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Sucursal no trobada amb el id: " + id));
    }

    public Sucursal save(Sucursal sucursal) {
        repo.save(sucursal);

        return getById(sucursal.getId());
    }

    public void delete(Sucursal s) {
        Sucursal sucursal = getById(s.getId());

        repo.deleteById(sucursal.getId());
    }

}
