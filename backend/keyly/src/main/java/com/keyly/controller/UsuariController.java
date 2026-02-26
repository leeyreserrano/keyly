package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Departament;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.Usuari;
import com.keyly.service.DepartamentService;
import com.keyly.service.RolService;
import com.keyly.service.SucursalService;
import com.keyly.service.UsuariService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UsuariController {

    @Autowired
    private UsuariService service;

    @Autowired
    private SucursalService sucursalService;

    @Autowired
    private DepartamentService departamentService;

    @Autowired
    private RolService rolService;

    @GetMapping("usuaris")
    public ResponseEntity<List<Usuari>> getAllUsuaris() {
        return ResponseEntity.ok(service.getAllUsuaris());
    }

    @GetMapping("usuari/{id}")
    public ResponseEntity<Usuari> getUsuari(@PathVariable Long id) {
        Usuari usuari = service.getById(id);

        if (usuari == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuari);
    }
    
    @PostMapping("usuari")
    public ResponseEntity<Usuari> addUsuari(@RequestBody Usuari u) {
        Usuari usuari = service.save(u);

        if (usuari == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(usuari);
    }

    @PutMapping("usuari/{id}")
    public ResponseEntity<Usuari> updateUsuari(@PathVariable Long id, @RequestBody Usuari usuariActualitzat) {
        Usuari usuari = service.getById(id);

        Sucursal s = sucursalService.getById(usuariActualitzat.getSucursal().getId());
        Departament d = departamentService.getById(usuariActualitzat.getDepartament().getId());
        Rol r = rolService.getById(usuariActualitzat.getRol().getId());

        if (usuari == null || s == null || d == null || r == null) {
            return ResponseEntity.notFound().build();
        }

        usuari.setSucursal(usuariActualitzat.getSucursal());
        usuari.setDepartament(usuariActualitzat.getDepartament());
        usuari.setRol(usuariActualitzat.getRol());
        usuari.setNom(usuariActualitzat.getNom());
        usuari.setCorreu(usuariActualitzat.getCorreu());
        usuari.setContrasenya(usuariActualitzat.getContrasenya());
        usuari.setPotAdministrar(usuariActualitzat.isPotAdministrar());

        Usuari usuariGuardat = service.save(usuari);

        return ResponseEntity.ok(usuariGuardat);
    }

    @DeleteMapping("usuari/{id}")
    public ResponseEntity<Usuari> deleteUsuari(@PathVariable Long id) {
        Usuari usuari = service.getById(id);

        if (usuari == null) {
            return ResponseEntity.notFound().build();
        }

        service.delete(usuari);

        return ResponseEntity.ok(usuari);
    }

    // TODO Login provisional para ir probando 
    @GetMapping("login/{id}")
    public ResponseEntity<Boolean> loginProvisional(@PathVariable Long id, @RequestParam String contrasenya) {
        boolean faLogin = service.login(id, contrasenya);

        if (faLogin) {
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.notFound().build();
    }
    
}
