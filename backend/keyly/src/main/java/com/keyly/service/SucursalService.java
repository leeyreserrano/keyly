package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.model.Sucursal;
import com.keyly.repo.SucursalRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SucursalService {

    @Autowired
    private SucursalRepo repo;

    public List<Sucursal> getAllSucursals() {
        return repo.findAll();
    }

    public Sucursal getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Sucursal save(Sucursal sucursal) {
        repo.save(sucursal);

        return getById(sucursal.getId());
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

}
