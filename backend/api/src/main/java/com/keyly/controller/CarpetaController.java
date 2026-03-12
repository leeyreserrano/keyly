package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.CarpetaRequest;
import com.keyly.model.response.CarpetaResponse;
import com.keyly.model.response.ItemResponse;
import com.keyly.service.CarpetaService;

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
@Tag(name = "Carpeta Controller", description = "Operacions sobre carpetes")
public class CarpetaController {

    @Autowired
    private CarpetaService service;

    @Operation(summary = "Obté totes les carpetes")
    @GetMapping("carpetes")
    public ResponseEntity<List<CarpetaResponse>> getAllCarpetes() {
        return ResponseEntity.ok(service.getAllCarpetes());
    }

    @Operation(summary = "Obté una carpeta per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta trobada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @GetMapping("carpeta/{uuid}")
    public ResponseEntity<CarpetaResponse> getCarpeta(@PathVariable UUID uuid) {
        CarpetaResponse carpeta = service.getByUuid(uuid);

        return ResponseEntity.ok(carpeta);
    }

    @Operation(summary = "Obté els items dins d'una carpeta per UUID de la carpeta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta trobada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @GetMapping("carpeta/{uuid}/item")
    public ResponseEntity<List<ItemResponse>> getCarpetaItems(@PathVariable UUID uuid) {
        List<ItemResponse> items = service.getCarpetaItem(uuid);

        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Crea una carpeta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carpeta creada"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @PostMapping("carpeta")
    public ResponseEntity<CarpetaResponse> addCarpeta(@RequestBody CarpetaRequest c) {
        CarpetaResponse carpeta = service.save(c);

        return ResponseEntity.status(HttpStatus.CREATED).body(carpeta);
    }

    @Operation(summary = "Crea un llistat de carpetes")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carpetes creades"),
        @ApiResponse(responseCode = "404", description = "Un dels baguls no trobats")
    })
    @PostMapping("carpetes")
    public ResponseEntity<List<CarpetaResponse>> addCarpetes(@RequestBody List<CarpetaRequest> cs) {
        List<CarpetaResponse> responses = cs
                .stream()
                .map(carpeta -> service.save(carpeta))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Afegeix un item existent en una carpeta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item afegit"),
        @ApiResponse(responseCode = "404", description = "Carpeta o item no trobats")
    })
    @PostMapping("carpeta/{carpetaUuid}/item/{itemUuid}")
    public ResponseEntity<CarpetaResponse> addItemToCarpeta(@PathVariable UUID carpetaUuid,
            @PathVariable UUID itemUuid) {
        CarpetaResponse c = service.saveItemToCarpeta(carpetaUuid, itemUuid);

        return ResponseEntity.ok(c);
    }

    @Operation(summary = "Actualitza una carpeta per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta actualitzada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @PutMapping("carpeta/{uuid}")
    public ResponseEntity<CarpetaResponse> updateCarpeta(@PathVariable UUID uuid,
            @RequestBody CarpetaRequest carpetaActualitzada) {

        CarpetaResponse carpetaGuardada = service.update(uuid, carpetaActualitzada);

        return ResponseEntity.ok(carpetaGuardada);
    }

    @Operation(summary = "Elimina una carpeta per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta eliminada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @DeleteMapping("carpeta/{uuid}")
    public ResponseEntity<CarpetaResponse> deleteCarpeta(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    @Operation(summary = "Treu un item d'una carpeta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item tret"),
        @ApiResponse(responseCode = "404", description = "Carpeta o item no trobats")
    })
    @DeleteMapping("carpeta/{carpetaUuid}/item/{itemUuid}")
    public ResponseEntity<HttpStatus> deleteItemInCarpeta(@PathVariable UUID carpetaUuid, @PathVariable UUID itemUuid) {
        service.deleteItemInCarpeta(carpetaUuid, itemUuid);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Obté una carpeta per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta trobada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @Deprecated
    @GetMapping("carpeta/id/{id}")
    public ResponseEntity<CarpetaResponse> getCarpeta(@PathVariable Long id) {
        CarpetaResponse carpeta = service.getById(id);

        return ResponseEntity.ok(carpeta);
    }

    @Operation(summary = "Obté tots els items dins d'una carpeta per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Items dins de la carpeta"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @Deprecated
    @GetMapping("carpeta/id/{id}/item")
    public ResponseEntity<List<ItemResponse>> getCarpetaItems(@PathVariable Long id) {
        List<ItemResponse> items = service.getCarpetaItem(id);

        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Actualitza una carpeta per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta actualitzada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @Deprecated
    @PutMapping("carpeta/id/{id}")
    public ResponseEntity<CarpetaResponse> updateCarpeta(@PathVariable Long id,
            @RequestBody CarpetaRequest carpetaActualitzada) {

        CarpetaResponse carpetaGuardada = service.update(id, carpetaActualitzada);

        return ResponseEntity.ok(carpetaGuardada);
    }

    @Operation(summary = "Elimina una carpeta per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta eliminada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @Deprecated
    @DeleteMapping("carpeta/id/{id}")
    public ResponseEntity<CarpetaResponse> deleteCarpeta(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

    @Operation(summary = "Elimina un item dins d'una carpeta", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item eliminat"),
        @ApiResponse(responseCode = "404", description = "Carpeta o item no trobats")
    })
    @Deprecated
    @DeleteMapping("carpeta/id/{carpetaId}/item/{itemId}")
    public ResponseEntity<HttpStatus> deleteItemInCarpeta(@PathVariable Long carpetaId, @PathVariable Long itemId) {
        service.deleteItemInCarpeta(carpetaId, itemId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

}
