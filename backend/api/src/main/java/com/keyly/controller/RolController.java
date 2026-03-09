package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.RolRequest;
import com.keyly.model.response.RolResponse;
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
    public ResponseEntity<List<RolResponse>> getAllRols() {
        return ResponseEntity.ok(service.getAllRols());
    }

    @GetMapping("rol/{uuid}")
    public ResponseEntity<RolResponse> getRol(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }
    

    @PostMapping("rol")
    public ResponseEntity<RolResponse> addRol(@RequestBody RolRequest r) {
        RolResponse rol = service.save(r);

        return ResponseEntity.status(HttpStatus.CREATED).body(rol);
    }

    @PostMapping("rols")
    public ResponseEntity<List<RolResponse>> addRols(@RequestBody List<RolRequest> rs) {
        List<RolResponse> responses = rs
                .stream()
                .map(rol -> service.save(rol))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PutMapping("rol/{uuid}")
    public ResponseEntity<RolResponse> updateRol(@PathVariable UUID uuid, @RequestBody RolRequest request) {
        RolResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("rol/{uuid}")
    public ResponseEntity<RolResponse> deleteRol(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    @GetMapping("rol/id/{id}")
    public ResponseEntity<RolResponse> getRol(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Deprecated
    @PutMapping("rol/id/{id}")
    public ResponseEntity<RolResponse> updateRol(@PathVariable Long id, @RequestBody RolRequest rolActualitzat) {
        RolResponse response = service.update(id, rolActualitzat);

        return ResponseEntity.ok(response);
    }

    @Deprecated
    @DeleteMapping("rol/id/{id}")
    public ResponseEntity<RolResponse> deleteRol(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

}
