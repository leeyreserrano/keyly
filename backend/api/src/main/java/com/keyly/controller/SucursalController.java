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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class SucursalController {

    @Autowired
    private SucursalService service;

    @GetMapping("sucursals")
    public ResponseEntity<List<SucursalResponse>> getSucursals() {
        return ResponseEntity.ok(service.getAllSucursals());
    }

    @GetMapping("sucursal/{uuid}")
    public ResponseEntity<SucursalResponse> getSucursal(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }

    @PostMapping("sucursal")
    public ResponseEntity<SucursalResponse> addSucursal(@RequestBody SucursalRequest s) {
        SucursalResponse novaSucursal = service.save(s);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaSucursal);
    }

    @PostMapping("sucursals")
    public ResponseEntity<List<SucursalResponse>> addSucursals(@RequestBody List<SucursalRequest> sucursalsRequests) {
        List<SucursalResponse> sucursalResponses = new ArrayList<>();

        for (SucursalRequest s : sucursalsRequests) {
            sucursalResponses.add(service.save(s));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalResponses);
    }

    @PutMapping("sucursal/{uuid}")
    public ResponseEntity<SucursalResponse> updateSucursal(@PathVariable UUID uuid,
            @RequestBody SucursalRequest sucursalActualitzada) {
        SucursalResponse response = service.update(uuid, sucursalActualitzada);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("sucursal/{uuid}")
    public ResponseEntity<SucursalResponse> deleteSucursal(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    @GetMapping("sucursal/id/{id}")
    public ResponseEntity<SucursalResponse> getSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Deprecated
    @PutMapping("sucursal/id/{id}")
    public ResponseEntity<SucursalResponse> updateSucursal(@PathVariable Long id,
            @RequestBody SucursalRequest sucursalActualitzada) {
        SucursalResponse response = service.update(id, sucursalActualitzada);

        return ResponseEntity.ok(response);
    }

    @Deprecated
    @DeleteMapping("sucursal/id/{id}")
    public ResponseEntity<HttpStatus> deleteSucursal(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

}
