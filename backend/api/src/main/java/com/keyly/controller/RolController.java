package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Rol;
import com.keyly.service.RolService;

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

    @GetMapping("rols")
    public ResponseEntity<List<Rol>> getAllRols() {
        return ResponseEntity.ok(service.getAllRols());
    }

    @GetMapping("rol/{id}")
    public ResponseEntity<Rol> getRol(@PathVariable Long id) {
        Rol rol = service.getById(id);

        return ResponseEntity.ok(rol);
    }

    @PostMapping("rol")
    public ResponseEntity<Rol> addRol(@RequestBody Rol r) {
        Rol rol = service.save(r);

        return ResponseEntity.status(HttpStatus.CREATED).body(rol);
    }

    @PostMapping("rols")
    public ResponseEntity<List<Rol>> addRols(@RequestBody List<Rol> rs) {
        for (Rol r : rs) {
            service.save(r);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    @PutMapping("rol/{id}")
    public ResponseEntity<Rol> updateRol(@PathVariable Long id, @RequestBody Rol rolActualitzat) {
        Rol rol = service.getById(id);

        rol.setNom(rolActualitzat.getNom());
        rol.setSucursal(rolActualitzat.getSucursal());

        Rol rolGuardat = service.save(rol);

        return ResponseEntity.ok(rolGuardat);
    }

    @DeleteMapping("rol/{id}")
    public ResponseEntity<Rol> deleteRol(@PathVariable Long id) {
        Rol rol = service.getById(id);

        service.delete(rol);

        return ResponseEntity.ok(rol);
    }

}
