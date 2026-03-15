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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@Tag(name = "Departament Controller", description = "Operacions sobre departaments")
public class DepartamentController {

    @Autowired
    private DepartamentService service;

    @Operation(summary = "Obté tots els departaments")
    @GetMapping("departaments")
    public ResponseEntity<List<DepartamentResponse>> getAllDepartaments() {
        return ResponseEntity.ok(service.getAllDepartaments());
    }

    @Operation(summary = "Obté un departament per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departament trobat"),
        @ApiResponse(responseCode = "404", description = "Departament no trobat")
    })
    @GetMapping("departament/{uuid}")
    public ResponseEntity<DepartamentResponse> getDepartament(@PathVariable UUID uuid) {
        DepartamentResponse departament = service.getByUuid(uuid);

        return ResponseEntity.ok(departament);
    }

    @Operation(summary = "Crea un departament")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Departament creat"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PostMapping("departament")
    public ResponseEntity<DepartamentResponse> addDepartament(@RequestBody DepartamentRequest d) {
        DepartamentResponse departament = service.save(d);

        return ResponseEntity.status(HttpStatus.CREATED).body(departament);
    }

    @Operation(summary = "Crea un llistat de departaments")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Departaments creats"),
        @ApiResponse(responseCode = "404", description = "Una de les sucursals no trobades")
    })
    @PostMapping("departaments")
    public ResponseEntity<List<DepartamentResponse>> addDepartaments(@RequestBody List<DepartamentRequest> ds) {
        List<DepartamentResponse> responses = new ArrayList<>();
        for (DepartamentRequest d : ds) {
            responses.add(service.save(d));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Actualitza un departament per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departament actualitzat"),
        @ApiResponse(responseCode = "404", description = "Departament o sucursal no trobats")
    })
    @PutMapping("departament/{uuid}")
    public ResponseEntity<DepartamentResponse> updateDepartament(@PathVariable UUID uuid, @RequestBody DepartamentRequest request) {
        DepartamentResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un departament per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departament eliminat"),
        @ApiResponse(responseCode = "404", description = "Departament no trobat")
    })
    @DeleteMapping("departament/{uuid}")
    public ResponseEntity<DepartamentResponse> deleteDepartament(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Obté un departament per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departament trobat"),
        @ApiResponse(responseCode = "404", description = "Departament no trobat")
    })
    @Deprecated
    @GetMapping("departament/id/{id}")
    public ResponseEntity<DepartamentResponse> getDepartament(@PathVariable Long id) {
        DepartamentResponse departament = service.getById(id);

        return ResponseEntity.ok(departament);
    }

    @Operation(summary = "Actualitza un departament per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departament actualitzat"),
        @ApiResponse(responseCode = "404", description = "Departament o sucursal no trobats")
    })
    @Deprecated
    @PutMapping("departament/id/{id}")
    public ResponseEntity<DepartamentResponse> updateDepartament(@PathVariable Long id,
            @RequestBody DepartamentRequest departamentActualitzat) {
        DepartamentResponse response = service.update(id, departamentActualitzat);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un departament per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departament eliminat"),
        @ApiResponse(responseCode = "404", description = "Departament no trobat")
    })
    @Deprecated
    @DeleteMapping("departament/id/{id}")
    public ResponseEntity<DepartamentResponse> deleteDepartament(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

}
