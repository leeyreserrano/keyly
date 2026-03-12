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
@Tag(name = "Rol Controller", description = "Operacions sobre rols")
public class RolController {

    @Autowired
    private RolService service;

    @Operation(summary = "Obté tots els rols")
    @GetMapping("rols")
    public ResponseEntity<List<RolResponse>> getAllRols() {
        return ResponseEntity.ok(service.getAllRols());
    }

    @Operation(summary = "Obté un rol per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol trobat"),
        @ApiResponse(responseCode = "404", description = "Rol no trobat")
    })
    @GetMapping("rol/{uuid}")
    public ResponseEntity<RolResponse> getRol(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }
    
    @Operation(summary = "Crea un rol")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Rol creat"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PostMapping("rol")
    public ResponseEntity<RolResponse> addRol(@RequestBody RolRequest r) {
        RolResponse rol = service.save(r);

        return ResponseEntity.status(HttpStatus.CREATED).body(rol);
    }

    @Operation(summary = "Crea un llistat de rols")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Rols creats"),
        @ApiResponse(responseCode = "404", description = "Una de les sucursals no trobades")
    })
    @PostMapping("rols")
    public ResponseEntity<List<RolResponse>> addRols(@RequestBody List<RolRequest> rs) {
        List<RolResponse> responses = rs
                .stream()
                .map(rol -> service.save(rol))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Actualitza un rol per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol actualitzat"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PutMapping("rol/{uuid}")
    public ResponseEntity<RolResponse> updateRol(@PathVariable UUID uuid, @RequestBody RolRequest request) {
        RolResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un rol per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol eliminat"),
        @ApiResponse(responseCode = "404", description = "Rol no trobat")
    })
    @DeleteMapping("rol/{uuid}")
    public ResponseEntity<RolResponse> deleteRol(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Obté un rol per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol trobat"),
        @ApiResponse(responseCode = "404", description = "Rol no trobat")
    })
    @Deprecated
    @GetMapping("rol/id/{id}")
    public ResponseEntity<RolResponse> getRol(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Actualitza un rol per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol actualitzat"),
        @ApiResponse(responseCode = "404", description = "Rol no trobat")
    })
    @Deprecated
    @PutMapping("rol/id/{id}")
    public ResponseEntity<RolResponse> updateRol(@PathVariable Long id, @RequestBody RolRequest rolActualitzat) {
        RolResponse response = service.update(id, rolActualitzat);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un rol per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol eliminat"),
        @ApiResponse(responseCode = "404", description = "Rol no trobat")
    })
    @Deprecated
    @DeleteMapping("rol/id/{id}")
    public ResponseEntity<RolResponse> deleteRol(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

}
