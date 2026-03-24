package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.CarpetaRequest;
import com.keyly.model.response.CarpetaResponse;
import com.keyly.model.response.ItemResponse;
import com.keyly.service.CarpetaService;
import com.keyly.service.UsuariService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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

    @Autowired
    private UsuariService usuariService;

    @Operation(summary = "Obté totes les carpetes", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("carpetes/all")
    public ResponseEntity<List<CarpetaResponse>> getAllCarpetes() {
        return ResponseEntity.ok(service.getAllCarpetes());
    }

    @Operation(summary = "Obté totes les carpetes de l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("carpetes")
    public ResponseEntity<List<CarpetaResponse>> getCarpetes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(service.getAllCarpetesByUsuariUuid(UUID.fromString(authentication.getName())));
    }

    @Operation(summary = "Obté una carpeta per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta trobada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("carpeta/admin/{uuid}")
    public ResponseEntity<CarpetaResponse> getCarpeta(@PathVariable UUID uuid) {
        CarpetaResponse carpeta = service.getByUuid(uuid);

        return ResponseEntity.ok(carpeta);
    }

    @Operation(summary = "Obté una carpeta de l'usuari per UUID", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta trobada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("carpeta/{uuid}")
    public ResponseEntity<CarpetaResponse> getUserCarpeta(@PathVariable UUID uuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CarpetaResponse response = service
                .getUserCarpeta(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), uuid);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obté els items dins d'una carpeta per UUID de la carpeta", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta trobada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("carpeta/admin/{uuid}/item")
    public ResponseEntity<List<ItemResponse>> getCarpetaItems(@PathVariable UUID uuid) {
        List<ItemResponse> items = service.getCarpetaItem(uuid);

        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Obté els items dins d'una carpeta de l'usuari per UUID de la carpeta", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta trobada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("carpeta/{uuid}/item")
    public ResponseEntity<List<ItemResponse>> getUserCarpetaItems(@PathVariable UUID uuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<ItemResponse> items = service.getUserCarpetaItem(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), uuid);

        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Crea una carpeta", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carpeta creada"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("carpeta/admin")
    public ResponseEntity<CarpetaResponse> addCarpeta(@RequestBody CarpetaRequest c) {
        CarpetaResponse carpeta = service.save(c);

        return ResponseEntity.status(HttpStatus.CREATED).body(carpeta);
    }

    @Operation(summary = "Crea una carpeta a l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carpeta creada"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PostMapping("carpeta")
    public ResponseEntity<CarpetaResponse> addUserCarpeta(@RequestBody CarpetaRequest c) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CarpetaResponse carpeta = service
                .save(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), c);

        return ResponseEntity.status(HttpStatus.CREATED).body(carpeta);
    }

    @Operation(summary = "Afegeix un item existent en una carpeta", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item afegit"),
        @ApiResponse(responseCode = "404", description = "Carpeta o item no trobats")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("carpeta/admin/{carpetaUuid}/item/{itemUuid}")
    public ResponseEntity<CarpetaResponse> addItemToCarpeta(@PathVariable UUID carpetaUuid,
            @PathVariable UUID itemUuid) {
        CarpetaResponse c = service.saveItemToCarpeta(carpetaUuid, itemUuid);

        return ResponseEntity.ok(c);
    }

    @Operation(summary = "Afegeix un item existent en una carpeta de l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item afegit"),
        @ApiResponse(responseCode = "404", description = "Carpeta o item no trobats")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PostMapping("carpeta/{carpetaUuid}/item/{itemUuid}")
    public ResponseEntity<CarpetaResponse> addItemToUserCarpeta(@PathVariable UUID carpetaUuid,
            @PathVariable UUID itemUuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CarpetaResponse c = service.saveItemToUserCarpeta(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), carpetaUuid, itemUuid);

        return ResponseEntity.ok(c);
    }

    @Operation(summary = "Actualitza una carpeta per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta actualitzada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("carpeta/admin/{uuid}")
    public ResponseEntity<CarpetaResponse> updateCarpeta(@PathVariable UUID uuid,
            @RequestBody CarpetaRequest carpetaActualitzada) {

        CarpetaResponse carpetaGuardada = service.update(uuid, carpetaActualitzada);

        return ResponseEntity.ok(carpetaGuardada);
    }

    @Operation(summary = "Actualitza una carpeta de l'usuari per UUID", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta actualitzada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PutMapping("carpeta/{uuid}")
    public ResponseEntity<CarpetaResponse> updateUserCarpeta(@PathVariable UUID uuid,
            @RequestBody CarpetaRequest carpetaActualitzada) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CarpetaResponse carpeta = service.update(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())),
                uuid, carpetaActualitzada);

        return ResponseEntity.ok(carpeta);
    }

    @Operation(summary = "Elimina una carpeta per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta eliminada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("carpeta/admin/{uuid}")
    public ResponseEntity<Void> deleteCarpeta(@PathVariable UUID uuid) {
        service.deleteByUuid(uuid);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Elimina una carpeta de l'usuari per UUID", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta eliminada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @DeleteMapping("carpeta/{uuid}")
    public ResponseEntity<Void> deleteUserCarpeta(@PathVariable UUID uuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        service.deleteUserCarpeta(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), uuid);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Treu un item d'una carpeta", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item tret"),
        @ApiResponse(responseCode = "404", description = "Carpeta o item no trobats")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("carpeta/admin/{carpetaUuid}/item/{itemUuid}")
    public ResponseEntity<HttpStatus> deleteItemInCarpeta(@PathVariable UUID carpetaUuid, @PathVariable UUID itemUuid) {
        service.deleteItemInCarpeta(carpetaUuid, itemUuid);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @Operation(summary = "Treu un item d'una carpeta de l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item tret"),
        @ApiResponse(responseCode = "404", description = "Carpeta o item no trobats")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @DeleteMapping("carpeta/{carpetaUuid}/item/{itemUuid}")
    public ResponseEntity<Void> deleteItemInUserCarpeta(@PathVariable UUID carpetaUuid, @PathVariable UUID itemUuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        service.deleteItemInUserCarpeta(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), carpetaUuid, itemUuid);

        return ResponseEntity.ok().build();
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Crea un llistat de carpetes")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carpetes creades"),
        @ApiResponse(responseCode = "404", description = "Un dels baguls no trobats")
    })
    @Deprecated
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("carpetes")
    public ResponseEntity<List<CarpetaResponse>> addCarpetes(@RequestBody List<CarpetaRequest> cs) {
        List<CarpetaResponse> responses = cs
                .stream()
                .map(carpeta -> service.save(carpeta))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Obté una carpeta per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carpeta trobada"),
        @ApiResponse(responseCode = "404", description = "Carpeta no trobada")
    })
    @Deprecated
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("carpeta/id/{carpetaId}/item/{itemId}")
    public ResponseEntity<HttpStatus> deleteItemInCarpeta(@PathVariable Long carpetaId, @PathVariable Long itemId) {
        service.deleteItemInCarpeta(carpetaId, itemId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

}
