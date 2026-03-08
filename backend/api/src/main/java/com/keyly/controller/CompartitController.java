package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Compartit;
import com.keyly.model.request.CompartitRequest;
import com.keyly.model.response.CompartitResponse;
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

    @GetMapping("compartit/{uuid}")
    public ResponseEntity<CompartitResponse> getCompartit(@PathVariable UUID uuid) {
        CompartitResponse compartit = service.getByUuid(uuid);

        return ResponseEntity.ok(compartit);
    }

    @PostMapping("compartit")
    public ResponseEntity<CompartitResponse> addCompartit(@RequestBody CompartitRequest c) {
        CompartitResponse compartit = service.save(c);

        return ResponseEntity.status(HttpStatus.CREATED).body(compartit);
    }

    @PostMapping("compartits")
    public ResponseEntity<List<CompartitResponse>> addCompartits(@RequestBody List<CompartitRequest> cs) {
        List<CompartitResponse> responses = cs
                .stream()
                .map(compartit -> service.save(compartit))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PutMapping("compartit/{uuid}")
    public ResponseEntity<CompartitResponse> updateCompartit(@PathVariable UUID uuid,
            @RequestBody CompartitRequest compartitActualitzat) {
        return ResponseEntity.ok(service.update(uuid, compartitActualitzat));
    }

    @DeleteMapping("compartit/{uuid}")
    public ResponseEntity<Compartit> deleteCompartit(@PathVariable UUID uuid) {
        service.deleteByUuid(uuid);

        return ResponseEntity.ok(null);
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    @GetMapping("compartit/id/{id}")
    public ResponseEntity<CompartitResponse> getCompartit(@PathVariable Long id) {
        CompartitResponse compartit = service.getById(id);

        return ResponseEntity.ok(compartit);
    }

    @Deprecated
    @PutMapping("compartit/id/{id}")
    public ResponseEntity<CompartitResponse> updateCompartit(@PathVariable Long id,
            @RequestBody CompartitRequest compartitActualitzat) {
        CompartitResponse compartitGuardat = service.update(id, compartitActualitzat);

        return ResponseEntity.ok(compartitGuardat);
    }

    @Deprecated
    @DeleteMapping("compartit/id/{id}")
    public ResponseEntity<Compartit> deleteCompartit(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.ok(null);
    }

}
