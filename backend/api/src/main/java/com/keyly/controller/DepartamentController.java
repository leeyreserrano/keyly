package com.keyly.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.DepartamentRequest;
import com.keyly.model.response.DepartamentResponse;
import com.keyly.service.DepartamentService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class DepartamentController {

    @Autowired
    private DepartamentService service;

    @GetMapping("departaments")
    public ResponseEntity<List<DepartamentResponse>> getAllDepartaments() {
        return ResponseEntity.ok(service.getAllDepartaments());
    }

    @GetMapping("departament/{uuid}")
    public ResponseEntity<DepartamentResponse> getDepartament(@PathVariable UUID uuid) {
        DepartamentResponse departament = service.getByUuid(uuid);

        return ResponseEntity.ok(departament);
    }

    @PostMapping("departament")
    public ResponseEntity<DepartamentResponse> addDepartament(@RequestBody DepartamentRequest d) {
        DepartamentResponse departament = service.save(d);

        return ResponseEntity.status(HttpStatus.CREATED).body(departament);
    }

    @PostMapping("departaments")
    public ResponseEntity<List<DepartamentResponse>> addDepartaments(@RequestBody List<DepartamentRequest> ds) {
        List<DepartamentResponse> responses = new ArrayList<>();
        for (DepartamentRequest d : ds) {
            responses.add(service.save(d));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PutMapping("departament/{uuid}")
    public ResponseEntity<DepartamentResponse> updateDepartament(@PathVariable UUID uuid, @RequestBody DepartamentRequest request) {
        DepartamentResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("departament/{uuid}")
    public ResponseEntity<DepartamentResponse> deleteDepartament(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    @GetMapping("departament/id/{id}")
    public ResponseEntity<DepartamentResponse> getDepartament(@PathVariable Long id) {
        DepartamentResponse departament = service.getById(id);

        return ResponseEntity.ok(departament);
    }

    @Deprecated
    @PutMapping("departament/id/{id}")
    public ResponseEntity<DepartamentResponse> updateDepartament(@PathVariable Long id,
            @RequestBody DepartamentRequest departamentActualitzat) {
        DepartamentResponse response = service.update(id, departamentActualitzat);

        return ResponseEntity.ok(response);
    }

    @Deprecated
    @DeleteMapping("departament/id/{id}")
    public ResponseEntity<DepartamentResponse> deleteDepartament(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.ok(null);
    }

}
