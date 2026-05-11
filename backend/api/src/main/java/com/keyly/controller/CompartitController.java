package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.enums.Permisos;
import com.keyly.model.request.CompartitRequest;
import com.keyly.model.request.combined.CombinedCarpetaRequestCompartitRequest;
import com.keyly.model.request.combined.CombinedItemRequestCompartitRequest;
import com.keyly.model.response.CompartitResponse;
import com.keyly.service.CompartitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/compartit")
@Tag(name = "Compartit Controller", description = "Operacions sobre la taula Compartits")
public class CompartitController {

    @Autowired
    private CompartitService service;

    @Operation(summary = "Obté tots els compartits", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("all/admin")
    public ResponseEntity<List<CompartitResponse>> getAllCompartits() {
        return ResponseEntity.ok(service.getAllCompartits());
    }

    @Operation(summary = "Obté tots els compartits que arriven l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("get/all")
    public ResponseEntity<List<CompartitResponse>> getAllCompartitsOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID usuariUuid = UUID.fromString(authentication.getName());

        return ResponseEntity.ok(service.getAllCompartitsOfUser(usuariUuid));
    }

    @Operation(summary = "Obté tots els compartits que crea l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("get/all/creats")
    public ResponseEntity<List<CompartitResponse>> getAllCompartitsOfUserCreats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID usuariUuid = UUID.fromString(authentication.getName());

        return ResponseEntity.ok(service.getAllCompartitsOfUserCreats(usuariUuid));
    }
    

    @Operation(summary = "Obté un compartit per UUID", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compartit trobat"),
            @ApiResponse(responseCode = "404", description = "Compartit no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("get/{uuid}")
    public ResponseEntity<CompartitResponse> getCompartit(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getCompartitByUuid(uuid));
    }

    @Operation(summary = "Crea compartits amb múltiples usuaris", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compartits creats"),
            @ApiResponse(responseCode = "400", description = "Dades invàlides"),
            @ApiResponse(responseCode = "404", description = "Entitat no trobada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PostMapping("add")
    public ResponseEntity<List<CompartitResponse>> createCompartit(@RequestBody CompartitRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID creadorUuid = UUID.fromString(authentication.getName());

        List<CompartitResponse> responses = service.createCompartit(creadorUuid, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Crea un item i el comparteix a múltiples usuaris", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item creat i compartit"),
            @ApiResponse(responseCode = "400", description = "Dades invàlides"),
            @ApiResponse(responseCode = "404", description = "Usuari o item no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PostMapping("add/item")
    public ResponseEntity<HttpStatus> createCompartit(@RequestBody CombinedItemRequestCompartitRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID creadorUuid = UUID.fromString(authentication.getName());

        service.createCompartit(creadorUuid, request.itemRequest(), request.compartitRequest());

        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @Operation(summary = "Crea una carpeta i la comparteix a múltiples usuaris", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Carpeta creada i compartida"),
            @ApiResponse(responseCode = "400", description = "Dades invàlides"),
            @ApiResponse(responseCode = "404", description = "Usuari o carpeta no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PostMapping("add/carpeta")
    public ResponseEntity<HttpStatus> createCompartit(@RequestBody CombinedCarpetaRequestCompartitRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID creadorUuid = UUID.fromString(authentication.getName());

        service.createCompartit(creadorUuid, request.carpetaRequest(), request.compartitRequest());

        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @Operation(summary = "Cambia el permis d'una entitat compartida", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Entitat editada"),
            @ApiResponse(responseCode = "404", description = "Entitat no trobada"),
            @ApiResponse(responseCode = "409", description = "L'usuari no té permisos suficients")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PutMapping("update/{compartitUuid}/{permisos}")
    public ResponseEntity<HttpStatus> updateCompartit(@PathVariable UUID compartitUuid, @PathVariable Permisos permisos) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID creadorUuid = UUID.fromString(authentication.getName());

        service.updateCompartit(creadorUuid, compartitUuid, permisos);

        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Elimina un compartit", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Compartit eliminat"),
            @ApiResponse(responseCode = "404", description = "Compartit no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @DeleteMapping("delete/{uuid}")
    public ResponseEntity<Void> deleteCompartit(@PathVariable UUID uuid) {
        service.deleteCompartit(uuid);
        return ResponseEntity.noContent().build();
    }

}
