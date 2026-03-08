package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Carpeta;
import com.keyly.model.request.CarpetaRequest;
import com.keyly.model.request.ItemRequest;
import com.keyly.model.response.CarpetaResponse;
import com.keyly.model.response.ItemResponse;
import com.keyly.service.CarpetaService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class CarpetaController {

    @Autowired
    private CarpetaService service;

    @GetMapping("carpetes")
    public ResponseEntity<List<CarpetaResponse>> getAllCarpetes() {
        return ResponseEntity.ok(service.getAllCarpetes());
    }

    @GetMapping("carpeta/{uuid}")
    public ResponseEntity<CarpetaResponse> getCarpeta(@PathVariable UUID uuid) {
        CarpetaResponse carpeta = service.getByUuid(uuid);

        return ResponseEntity.ok(carpeta);
    }

    @GetMapping("carpeta/{uuid}/item")
    public ResponseEntity<List<ItemResponse>> getCarpetaItems(@PathVariable UUID uuid) {
        List<ItemResponse> items = service.getCarpetaItem(uuid);

        return ResponseEntity.ok(items);
    }

    @PostMapping("carpeta")
    public ResponseEntity<CarpetaResponse> addCarpeta(@RequestBody CarpetaRequest c) {
        CarpetaResponse carpeta = service.save(c);

        return ResponseEntity.status(HttpStatus.CREATED).body(carpeta);
    }

    @PostMapping("carpetes")
    public ResponseEntity<List<CarpetaResponse>> addCarpetes(@RequestBody List<CarpetaRequest> cs) {
        List<CarpetaResponse> responses = cs
                .stream()
                .map(carpeta -> service.save(carpeta))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PostMapping("carpeta/{carpetaUuid}/item")
    public ResponseEntity<CarpetaResponse> addItemToCarpeta(@PathVariable UUID carpetaUuid,
            @RequestBody ItemRequest item) {
        CarpetaResponse c = service.saveItemToCarpeta(carpetaUuid, item);

        return ResponseEntity.ok(c);
    }

    @PutMapping("carpeta/{uuid}")
    public ResponseEntity<CarpetaResponse> updateCarpeta(@PathVariable UUID uuid,
            @RequestBody CarpetaRequest carpetaActualitzada) {

        CarpetaResponse carpetaGuardada = service.update(uuid, carpetaActualitzada);

        return ResponseEntity.ok(carpetaGuardada);
    }

    @DeleteMapping("carpeta/{uuid}")
    public ResponseEntity<CarpetaResponse> deleteCarpeta(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    @DeleteMapping("carpeta/{carpetaUuid}/item/{itemUuid}")
    public ResponseEntity<HttpStatus> deleteItemInCarpeta(@PathVariable UUID carpetaUuid, @PathVariable UUID itemUuid) {
        service.deleteItemInCarpeta(carpetaUuid, itemUuid);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    @GetMapping("carpeta/id/{id}")
    public ResponseEntity<CarpetaResponse> getCarpeta(@PathVariable Long id) {
        CarpetaResponse carpeta = service.getById(id);

        return ResponseEntity.ok(carpeta);
    }

    @Deprecated
    @GetMapping("carpeta/id/{id}/item")
    public ResponseEntity<List<ItemResponse>> getCarpetaItems(@PathVariable Long id) {
        List<ItemResponse> items = service.getCarpetaItem(id);

        return ResponseEntity.ok(items);
    }

    @Deprecated
    @PostMapping("carpeta/id/{carpetaId}/item")
    public ResponseEntity<CarpetaResponse> addItemToCarpeta(@PathVariable Long carpetaId,
            @RequestBody ItemRequest item) {
        CarpetaResponse c = service.saveItemToCarpeta(carpetaId, item);

        return ResponseEntity.ok(c);
    }

    @Deprecated
    @PutMapping("carpeta/id/{id}")
    public ResponseEntity<CarpetaResponse> updateCarpeta(@PathVariable Long id,
            @RequestBody CarpetaRequest carpetaActualitzada) {

        CarpetaResponse carpetaGuardada = service.update(id, carpetaActualitzada);

        return ResponseEntity.ok(carpetaGuardada);
    }

    @Deprecated
    @DeleteMapping("carpeta/id/{id}")
    public ResponseEntity<Carpeta> deleteCarpeta(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.ok(null);
    }

    @Deprecated
    @DeleteMapping("carpeta/id/{carpetaId}/item/{itemId}")
    public ResponseEntity<HttpStatus> deleteItemInCarpeta(@PathVariable Long carpetId, @PathVariable Long itemId) {
        service.deleteItemInCarpeta(carpetId, itemId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

}
