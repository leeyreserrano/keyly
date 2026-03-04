package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Compartit;
import com.keyly.service.CompartitService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class CompartitController {

    @Autowired
    private CompartitService service;

    @GetMapping("compartits")
    public ResponseEntity<List<Compartit>> getAllCompartits() {
        return ResponseEntity.ok(service.getAllCompartits());
    }

    @GetMapping("compartit/{id}")
    public ResponseEntity<Compartit> getCompartit(@PathVariable Long id) {
        Compartit compartit = service.getById(id);

        return ResponseEntity.ok(compartit);
    }

    @PostMapping("compartit")
    public ResponseEntity<Compartit> addCompartit(@RequestBody Compartit c) {
        Compartit compartit = service.save(c);

        return ResponseEntity.status(HttpStatus.CREATED).body(compartit);
    }

    @PostMapping("compartits")
    public ResponseEntity<List<Compartit>> addCompartits(@RequestBody List<Compartit> cs) {
        for (Compartit c : cs) {
            service.save(c);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(cs);
    }
    
    @PutMapping("compartit/{id}")
    public ResponseEntity<Compartit> updateCompartit(@PathVariable Long id, @RequestBody Compartit compartitActualitzat) {
        Compartit compartit = service.getById(id);

        compartit.setUsuari(compartitActualitzat.getUsuari());
        compartit.setTipusEntitat(compartitActualitzat.getTipusEntitat());
        compartit.setEntitatId(compartitActualitzat.getEntitatId());
        compartit.setPermisos(compartitActualitzat.getPermisos());

        Compartit compartitGuardat = service.save(compartit);

        return ResponseEntity.ok(compartitGuardat);
    }

    @DeleteMapping("compartit/{id}")
    public ResponseEntity<Compartit> deleteCompartit(@PathVariable Long id) {
        Compartit compartit = service.getById(id);

        service.delete(compartit);

        return ResponseEntity.ok(compartit);
    }

}
