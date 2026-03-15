package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Compartit;
import com.keyly.model.request.CompartitRequest;
import com.keyly.model.response.CompartitResponse;
import com.keyly.service.CompartitService;

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
@Tag(name = "Compartit Controller", description = "Operacions sobre la taula Compartits")
public class CompartitController {

    @Autowired
    private CompartitService service;

    @Operation(summary = "Obté totes les entitats compartides")
    @GetMapping("compartits")
    public ResponseEntity<List<Compartit>> getAllCompartits() {
        return ResponseEntity.ok(service.getAllCompartits());
    }

    @Operation(summary = "Obté una entitat compartida per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entitat compartida trobada"),
        @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @GetMapping("compartit/{uuid}")
    public ResponseEntity<CompartitResponse> getCompartit(@PathVariable UUID uuid) {
        CompartitResponse compartit = service.getByUuid(uuid);

        return ResponseEntity.ok(compartit);
    }

    @Operation(summary = "Crea una entitat compartida")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Entitat compartida creada"),
        @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    })
    @PostMapping("compartit")
    public ResponseEntity<CompartitResponse> addCompartit(@RequestBody CompartitRequest c) {
        CompartitResponse compartit = service.save(c);

        return ResponseEntity.status(HttpStatus.CREATED).body(compartit);
    }

    @Operation(summary = "Crea un llistat de entitats compartides")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Entitats compartides creades"),
        @ApiResponse(responseCode = "404", description = "Un dels usuaris no trobats")
    })
    @PostMapping("compartits")
    public ResponseEntity<List<CompartitResponse>> addCompartits(@RequestBody List<CompartitRequest> cs) {
        List<CompartitResponse> responses = cs
                .stream()
                .map(compartit -> service.save(compartit))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Actualitza una entitat compartida per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entitat compartida actualitzada"),
        @ApiResponse(responseCode = "404", description = "Entitat compartida no actualitzada")
    })
    @PutMapping("compartit/{uuid}")
    public ResponseEntity<CompartitResponse> updateCompartit(@PathVariable UUID uuid,
            @RequestBody CompartitRequest compartitActualitzat) {
        return ResponseEntity.ok(service.update(uuid, compartitActualitzat));
    }

    @Operation(summary = "Elimina una entitat compartida per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entitat compartida eliminada"),
        @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @DeleteMapping("compartit/{uuid}")
    public ResponseEntity<Compartit> deleteCompartit(@PathVariable UUID uuid) {
        service.deleteByUuid(uuid);

        return ResponseEntity.ok(null);
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Obté una entitat compartida per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entitat compartida trobada"),
        @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @Deprecated
    @GetMapping("compartit/id/{id}")
    public ResponseEntity<CompartitResponse> getCompartit(@PathVariable Long id) {
        CompartitResponse compartit = service.getById(id);

        return ResponseEntity.ok(compartit);
    }

    @Operation(summary = "Actualitza una entitat compartida per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entitat compartida actualitzada"),
        @ApiResponse(responseCode = "404", description = "Entitat compartida no actualitzada")
    })
    @Deprecated
    @PutMapping("compartit/id/{id}")
    public ResponseEntity<CompartitResponse> updateCompartit(@PathVariable Long id,
            @RequestBody CompartitRequest compartitActualitzat) {
        CompartitResponse compartitGuardat = service.update(id, compartitActualitzat);

        return ResponseEntity.ok(compartitGuardat);
    }

    @Operation(summary = "Elimina una entitat compartida per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entitat compartida eliminada"),
        @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @Deprecated
    @DeleteMapping("compartit/id/{id}")
    public ResponseEntity<Compartit> deleteCompartit(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.ok(null);
    }

}
