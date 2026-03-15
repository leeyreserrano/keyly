package com.keyly.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@Tag(name = "Sucursal Controller", description = "Operacions sobre sucursals")
public class SucursalController {

    @Autowired
    private SucursalService service;

    @Operation(summary = "Obté totes les sucursals")
    @GetMapping("sucursals")
    public ResponseEntity<List<SucursalResponse>> getSucursals() {
        return ResponseEntity.ok(service.getAllSucursals());
    }

    @Operation(summary = "Obté una sucursal per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal trobada"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @GetMapping("sucursal/{uuid}")
    public ResponseEntity<SucursalResponse> getSucursal(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }

    @Operation(summary = "Crea una sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Sucursal creada")
    })
    @PostMapping("sucursal")
    public ResponseEntity<SucursalResponse> addSucursal(@RequestBody SucursalRequest s) {
        SucursalResponse novaSucursal = service.save(s);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaSucursal);
    }

    @Operation(summary = "Crea un llistat de sucursals")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Sucursals creades")
    })
    @PostMapping("sucursals")
    public ResponseEntity<List<SucursalResponse>> addSucursals(@RequestBody List<SucursalRequest> sucursalsRequests) {
        List<SucursalResponse> sucursalResponses = new ArrayList<>();

        for (SucursalRequest s : sucursalsRequests) {
            sucursalResponses.add(service.save(s));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalResponses);
    }

    @Operation(summary = "Actualitza una sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal actualitzada"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PutMapping("sucursal/{uuid}")
    public ResponseEntity<SucursalResponse> updateSucursal(@PathVariable UUID uuid,
            @RequestBody SucursalRequest sucursalActualitzada) {
        SucursalResponse response = service.update(uuid, sucursalActualitzada);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina una sucursal per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal eliminada"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @DeleteMapping("sucursal/{uuid}")
    public ResponseEntity<SucursalResponse> deleteSucursal(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Obté una sucursal", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal trobada"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @Deprecated
    @GetMapping("sucursal/id/{id}")
    public ResponseEntity<SucursalResponse> getSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Actualitza una sucursal", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal actualitzada"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @Deprecated
    @PutMapping("sucursal/id/{id}")
    public ResponseEntity<SucursalResponse> updateSucursal(@PathVariable Long id,
            @RequestBody SucursalRequest sucursalActualitzada) {
        SucursalResponse response = service.update(id, sucursalActualitzada);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina una sucursal", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal eliminada"),
        @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @Deprecated
    @DeleteMapping("sucursal/id/{id}")
    public ResponseEntity<SucursalResponse> deleteSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

}
