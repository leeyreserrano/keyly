package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.RolRequest;
import com.keyly.model.response.RolResponse;
import com.keyly.service.RolService;

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
@RequestMapping("/rol")
@Tag(name = "Rol Controller", description = "Operacions sobre rols")
public class RolController {

    @Autowired
    private RolService service;

    @Operation(summary = "Obté tots els rols", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("all/admin")
    public ResponseEntity<List<RolResponse>> getAllRols() {
        return ResponseEntity.ok(service.getAllRols());
    }

    @Operation(summary = "Obté un rol per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol trobat"),
            @ApiResponse(responseCode = "404", description = "Rol no trobat")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("get/admin/{uuid}")
    public ResponseEntity<RolResponse> getRol(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }

    @Operation(summary = "Crea un rol", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rol creat"),
            @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("add/admin")
    public ResponseEntity<RolResponse> addRol(@RequestBody RolRequest r) {
        RolResponse rol = service.save(r);

        return ResponseEntity.status(HttpStatus.CREATED).body(rol);
    }

    @Operation(summary = "Actualitza un rol per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol actualitzat"),
            @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update/admin/{uuid}")
    public ResponseEntity<RolResponse> updateRol(@PathVariable UUID uuid, @RequestBody RolRequest request) {
        RolResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un rol per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol eliminat"),
            @ApiResponse(responseCode = "404", description = "Rol no trobat")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete/admin/{uuid}")
    public ResponseEntity<RolResponse> deleteRol(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }
    
}