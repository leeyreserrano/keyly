package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Sucursal;
import com.keyly.service.SucursalService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class SucursalController {

    @Autowired
    private SucursalService service;

    @GetMapping("sucursals")
    public ResponseEntity<List<Sucursal>> getSucursals() {
        return ResponseEntity.ok(service.getAllSucursals());
    }

    @GetMapping("sucursal/{id}")
    public ResponseEntity<Sucursal> getSucursal(@PathVariable Long id) {
        Sucursal sucursal = service.getById(id);

        return ResponseEntity.ok(sucursal);
    }

    @PostMapping("sucursal")
    public ResponseEntity<Sucursal> addSucursal(@RequestBody Sucursal s) {
        Sucursal novaSucursal = service.save(s);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaSucursal);
    }

    @PostMapping("sucursals")
    public ResponseEntity<List<Sucursal>> addSucursals(@RequestBody List<Sucursal> ss) {
        for (Sucursal s : ss) {
            service.save(s);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ss);
    }
    

    @PutMapping("sucursal/{id}")
    public ResponseEntity<Sucursal> updateSucursal(@PathVariable Long id, @RequestBody Sucursal sucursalActualitzada) {

        Sucursal sucursal = service.getById(id);

        sucursal.setNom(sucursalActualitzada.getNom());
        sucursal.setDireccio(sucursalActualitzada.getDireccio());
        sucursal.setCiutat(sucursalActualitzada.getCiutat());
        sucursal.setPais(sucursalActualitzada.getPais());
        sucursal.setTelefon(sucursalActualitzada.getTelefon());
        sucursal.setCorreu(sucursalActualitzada.getCorreu());

        Sucursal sucursalGuardada = service.save(sucursal);

        return ResponseEntity.ok(sucursalGuardada);
    }

    @DeleteMapping("sucursal/{id}")
    public ResponseEntity<Sucursal> deleteSucursal(@PathVariable Long id) {
        Sucursal sucursal = service.getById(id);

        service.delete(sucursal);

        return ResponseEntity.ok(sucursal);
    }

}
