package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.service.RolService;
import com.keyly.service.SucursalService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class RolController {

    @Autowired
    private RolService service;

    @Autowired
    private SucursalService sucursalService;

    @GetMapping("rols")
    public ResponseEntity<List<Rol>> getAllRols() {
        return ResponseEntity.ok(service.getAllRols());
    }

    @GetMapping("rol/{id}")
    public ResponseEntity<Rol> getRol(@PathVariable Long id) {
        Rol rol = service.getById(id);

        if (rol == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(rol);
    }
    
    @PostMapping("rol")
    public ResponseEntity<Rol> addRol(@RequestBody Rol r) {
        Rol rol = service.save(r);

        if (rol == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(rol);
    }

    @PutMapping("rol/{id}")
    public ResponseEntity<Rol> updateRol(@PathVariable Long id, @RequestBody Rol rolActualitzat) {
        Rol rol = service.getById(id);
        Sucursal sucursal = sucursalService.getById(rolActualitzat.getSucursal().getId());

        if (rol == null || sucursal == null) {
            return ResponseEntity.notFound().build();
        }

        rol.setNom(rolActualitzat.getNom());
        rol.setSucursal(rolActualitzat.getSucursal());
        
        Rol rolGuardat = service.save(rol);

        return ResponseEntity.ok(rolGuardat);
    }

    @DeleteMapping("rol/{id}")
    public ResponseEntity<Rol> deleteRol(@PathVariable Long id) {
        Rol rol = service.getById(id);

        if (rol == null) {
            return ResponseEntity.notFound().build();
        }

        service.delete(rol);

        return ResponseEntity.ok(rol);
    }

}
