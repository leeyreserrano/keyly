package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.DepartamentRequest;
import com.keyly.model.response.DepartamentResponse;
import com.keyly.service.DepartamentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/departament")
@Tag(name = "Departament Controller", description = "Operacions sobre departaments")
public class DepartamentController {

    @Autowired
    private DepartamentService service;

    @Operation(
        summary = "Obté tots els departaments",
        description = "ADMIN",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP')")
    @GetMapping("all/admin")
    public ResponseEntity<List<DepartamentResponse>> getAllDepartaments() {
        return ResponseEntity.ok(service.getAllDepartaments());
    }

    @Operation(
        summary = "Obté un departament per UUID",
        description = "ADMIN",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departament trobat"),
        @ApiResponse(responseCode = "404", description = "Departament no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP')")
    @GetMapping("get/admin/{uuid}")
    public ResponseEntity<DepartamentResponse> getDepartament(@PathVariable UUID uuid) {
        DepartamentResponse departament = service.getByUuid(uuid);

        return ResponseEntity.ok(departament);
    }

    @Operation(
        summary = "Crea un departament",
        description = "ADMIN",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Departament creat"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("add/admin")
    public ResponseEntity<DepartamentResponse> addDepartament(@RequestBody DepartamentRequest d) {
        DepartamentResponse departament = service.save(d);

        return ResponseEntity.status(HttpStatus.CREATED).body(departament);
    }

    @Operation(
        summary = "Actualitza un departament per UUID",
        description = "ADMIN",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departament actualitzat"),
        @ApiResponse(responseCode = "404", description = "Departament o sucursal no trobats")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update/admin/{uuid}")
    public ResponseEntity<DepartamentResponse> updateDepartament(@PathVariable UUID uuid, @RequestBody DepartamentRequest request) {
        DepartamentResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Elimina un departament per UUID",
        description = "ADMIN",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departament eliminat"),
        @ApiResponse(responseCode = "404", description = "Departament no trobat")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete/admin/{uuid}")
    public ResponseEntity<DepartamentResponse> deleteDepartament(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

}
