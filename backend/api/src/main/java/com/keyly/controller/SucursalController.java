package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.SucursalRequest;
import com.keyly.model.response.SucursalResponse;
import com.keyly.service.SucursalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/sucursal")
@Tag(name = "Sucursal Controller", description = "Operacions sobre sucursals")
public class SucursalController {

    @Autowired
    private SucursalService service;

    @Operation(summary = "Obté totes les sucursals", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("all/admin")
    public ResponseEntity<List<SucursalResponse>> getSucursals() {
        return ResponseEntity.ok(service.getAllSucursals());
    }

    @Operation(summary = "Obté una sucursal per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal trobada"),
            @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("get/admin/{uuid}")
    public ResponseEntity<SucursalResponse> getSucursal(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }

    @Operation(summary = "Crea una sucursal", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sucursal creada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("add/admin")
    public ResponseEntity<SucursalResponse> addSucursal(@RequestBody SucursalRequest s) {
        SucursalResponse novaSucursal = service.save(s);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaSucursal);
    }

    @Operation(summary = "Actualitza una sucursal", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal actualitzada"),
            @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update/admin/{uuid}")
    public ResponseEntity<SucursalResponse> updateSucursal(@PathVariable UUID uuid,
            @RequestBody SucursalRequest sucursalActualitzada) {
        SucursalResponse response = service.update(uuid, sucursalActualitzada);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina una sucursal per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal eliminada"),
            @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete/admin/{uuid}")
    public ResponseEntity<SucursalResponse> deleteSucursal(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

}