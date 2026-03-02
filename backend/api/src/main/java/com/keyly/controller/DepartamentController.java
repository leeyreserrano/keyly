package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Departament;
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
    public ResponseEntity<List<Departament>> getAllDepartaments() {
        return ResponseEntity.ok(service.getAllDepartaments());
    }

    @GetMapping("departament/{id}")
    public ResponseEntity<Departament> getDepartament(@PathVariable Long id) {
        Departament departament = service.getById(id);

        return ResponseEntity.ok(departament);

    }

    @PostMapping("departament")
    public ResponseEntity<Departament> addDepartament(@RequestBody Departament d) {
        Departament departament = service.save(d);

        return ResponseEntity.status(HttpStatus.CREATED).body(departament);
    }

    @PostMapping("departaments")
    public ResponseEntity<List<Departament>> addDepartaments(@RequestBody List<Departament> ds) {
        for (Departament d : ds) {
            service.save(d);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ds);
    }
    

    @PutMapping("departament/{id}")
    public ResponseEntity<Departament> updateDepartament(@PathVariable Long id,
            @RequestBody Departament departamentActualitzat) {

        Departament departament = service.getById(id);

        departament.setNom(departamentActualitzat.getNom());
        departament.setSucursal(departamentActualitzat.getSucursal());

        Departament departamentGuardat = service.save(departament);

        return ResponseEntity.ok(departamentGuardat);
    }

    @DeleteMapping("departament/{id}")
    public ResponseEntity<Departament> deleteDepartament(@PathVariable Long id) {
        Departament departament = service.getById(id);

        service.delete(departament);

        return ResponseEntity.ok(departament);
    }

}
