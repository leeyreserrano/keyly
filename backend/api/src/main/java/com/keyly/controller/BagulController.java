package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Bagul;
import com.keyly.service.BagulService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class BagulController {

    // TODO - Gestionar las contraseñas seguras

    @Autowired
    private BagulService service;

    @GetMapping("contrasenyes")
    public ResponseEntity<List<Bagul>> getAllContrasenyes() {
        return ResponseEntity.ok(service.getAllContrasenyes());
    }

    @GetMapping("bagul/{id}")
    public ResponseEntity<Bagul> getContrasenya(@PathVariable Long id) {
        Bagul bagul = service.getById(id);

        return ResponseEntity.ok(bagul);
    }

    @PostMapping("bagul")
    public ResponseEntity<Bagul> addBagul(@RequestBody Bagul b) {
        Bagul bagul = service.save(b);

        return ResponseEntity.status(HttpStatus.CREATED).body(bagul);
    }

    @PostMapping("baguls")
    public ResponseEntity<List<Bagul>> addBaguls(@RequestBody List<Bagul> bs) {
        for (Bagul b : bs) {
            service.save(b);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(bs);
    }

    @PutMapping("bagul/{id}")
    public ResponseEntity<Bagul> updateBagul(@PathVariable Long id, @RequestBody Bagul bagulActualitzat) {
        Bagul bagul = service.getById(id);

        bagul.setPropietari(bagulActualitzat.getPropietari());

        Bagul bagulGuardat = service.save(bagul);

        return ResponseEntity.ok(bagulGuardat);
    }

    @DeleteMapping("bagul/{id}")
    public ResponseEntity<Bagul> deleteBagul(@PathVariable Long id) {
        Bagul bagul = service.getById(id);

        service.delete(bagul);

        return ResponseEntity.ok(bagul);
    }

}
