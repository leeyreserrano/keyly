package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.BagulRequest;
import com.keyly.model.response.BagulResponse;
import com.keyly.service.BagulService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class BagulController {

    @Autowired
    private BagulService service;

    @GetMapping("contrasenyes")
    public ResponseEntity<List<BagulResponse>> getAllContrasenyes() {
        return ResponseEntity.ok(service.getAllContrasenyes());
    }

    @GetMapping("bagul/{uuid}")
    public ResponseEntity<BagulResponse> getBagul(@PathVariable UUID uuid) {
        BagulResponse bagul = service.getByUuid(uuid);

        return ResponseEntity.ok(bagul);
    }

    @PostMapping("bagul")
    public ResponseEntity<BagulResponse> addBagul(@RequestBody BagulRequest b) {
        BagulResponse bagul = service.save(b);

        return ResponseEntity.status(HttpStatus.CREATED).body(bagul);
    }

    @PostMapping("baguls")
    public ResponseEntity<List<BagulResponse>> addBaguls(@RequestBody List<BagulRequest> bs) {
        List<BagulResponse> responses = bs
                .stream()
                .map(request -> service.save(request))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PutMapping("bagul/{uuid}")
    public ResponseEntity<BagulResponse> updateBagul(@PathVariable UUID uuid, @RequestBody BagulRequest request) {
        BagulResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("bagul/{uuid}")
    public ResponseEntity<BagulResponse> deleteBagul(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    @GetMapping("bagul/id/{id}")
    public ResponseEntity<BagulResponse> getContrasenya(@PathVariable Long id) {
        BagulResponse bagul = service.getById(id);

        return ResponseEntity.ok(bagul);
    }

    @Deprecated
    @PutMapping("bagul/id/{id}")
    public ResponseEntity<BagulResponse> updateBagul(@PathVariable Long id, @RequestBody BagulRequest bagulActualitzat) {
        BagulResponse response = service.update(id, bagulActualitzat);

        return ResponseEntity.ok(response);
    }

        @Deprecated
    @DeleteMapping("bagul/id/{id}")
    public ResponseEntity<BagulResponse> deleteBagul(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.ok(null);
    }

}
