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
@Tag(name = "Bagul Controller", description = "Operacions sobre baguls")
public class BagulController {

    @Autowired
    private BagulService service;

    @Operation(summary = "Obté tots els baguls")
    @GetMapping("baguls")
    public ResponseEntity<List<BagulResponse>> getAllBaguls() {
        return ResponseEntity.ok(service.getAllBaguls());
    }

    @Operation(summary = "Obté un bagul per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bagul trobat"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @GetMapping("bagul/{uuid}")
    public ResponseEntity<BagulResponse> getBagul(@PathVariable UUID uuid) {
        BagulResponse bagul = service.getByUuid(uuid);

        return ResponseEntity.ok(bagul);
    }

    @Operation(summary = "Crea un bagul")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Bagul creat"),
        @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    })
    @PostMapping("bagul")
    public ResponseEntity<BagulResponse> addBagul(@RequestBody BagulRequest b) {
        BagulResponse bagul = service.save(b);

        return ResponseEntity.status(HttpStatus.CREATED).body(bagul);
    }

    @Operation(summary = "Crea un llistat de baguls")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Baguls creats"),
        @ApiResponse(responseCode = "404", description = "Un dels usuaris no trobats")
    })
    @PostMapping("baguls")
    public ResponseEntity<List<BagulResponse>> addBaguls(@RequestBody List<BagulRequest> bs) {
        List<BagulResponse> responses = bs
                .stream()
                .map(request -> service.save(request))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Actualitza un bagul per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bagul actualitzat"),
        @ApiResponse(responseCode = "404", description = "Bagul o usuari no trobats")
    })
    @PutMapping("bagul/{uuid}")
    public ResponseEntity<BagulResponse> updateBagul(@PathVariable UUID uuid, @RequestBody BagulRequest request) {
        BagulResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un bagul per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bagul eliminat"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @DeleteMapping("bagul/{uuid}")
    public ResponseEntity<BagulResponse> deleteBagul(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Obté un bagul per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bagul trobat"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @Deprecated
    @GetMapping("bagul/id/{id}")
    public ResponseEntity<BagulResponse> getContrasenya(@PathVariable Long id) {
        BagulResponse bagul = service.getById(id);

        return ResponseEntity.ok(bagul);
    }

    @Operation(summary = "Actualitza un bagul per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bagul actualitzat"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @Deprecated
    @PutMapping("bagul/id/{id}")
    public ResponseEntity<BagulResponse> updateBagul(@PathVariable Long id, @RequestBody BagulRequest bagulActualitzat) {
        BagulResponse response = service.update(id, bagulActualitzat);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un bagul per UUID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bagul elimina"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @Deprecated
    @DeleteMapping("bagul/id/{id}")
    public ResponseEntity<BagulResponse> deleteBagul(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

}
