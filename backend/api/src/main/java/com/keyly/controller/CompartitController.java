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

import com.keyly.model.request.CompartitRequest;
import com.keyly.model.response.CompartitResponse;
import com.keyly.service.CompartitService;
import com.keyly.service.UsuariService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/compartit")
@Tag(name = "Compartit Controller", description = "Operacions sobre la taula Compartits")
public class CompartitController {

    @Autowired
    private CompartitService service;

    @Autowired
    private UsuariService usuariService;

    @Operation(summary = "Obté totes les entitats compartides", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("all/admin")
    public ResponseEntity<List<CompartitResponse>> getAllCompartits() {
        return ResponseEntity.ok(service.getAllCompartits());
    }

    @Operation(summary = "Obté totes les entitats compartides de l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("get/all")
    public ResponseEntity<List<CompartitResponse>> getCompartits() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(service.getAllCompartitsByUsuariUuid(UUID.fromString(authentication.getName())));
    }

    @Operation(summary = "Obté una entitat compartida per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entitat compartida trobada"),
            @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("get/admin/{uuid}")
    public ResponseEntity<CompartitResponse> getCompartit(@PathVariable UUID uuid) {
        CompartitResponse compartit = service.getByUuid(uuid);

        return ResponseEntity.ok(compartit);
    }

    @Operation(summary = "Obté una entitat de l'usuari per UUID", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entitat compartida trobada"),
            @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("get/{uuid}")
    public ResponseEntity<CompartitResponse> getUserCompartit(@PathVariable UUID uuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CompartitResponse response = service
                .getUserCompartit(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), uuid);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crea una entitat compartida a l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entitat compartida creada"),
            @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PostMapping("add")
    public ResponseEntity<CompartitResponse> addCompartit(@RequestBody CompartitRequest c) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CompartitResponse compartit = service
                .save(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), c);

        return ResponseEntity.status(HttpStatus.CREATED).body(compartit);
    }

    @Operation(summary = "Actualitza una entitat compartida per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entitat compartida actualitzada"),
            @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update/admin/{uuid}")
    public ResponseEntity<CompartitResponse> updateCompartit(@PathVariable UUID uuid,
            @RequestBody CompartitRequest compartitActualitzat) {
        return ResponseEntity.ok(service.update(uuid, compartitActualitzat));
    }

    @Operation(summary = "Actualitza una entitat compartida de l'usuari per UUID", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entitat compartida actualitzada"),
            @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PutMapping("update/{uuid}")
    public ResponseEntity<CompartitResponse> updateUsuariCompartit(@PathVariable UUID uuid,
            @RequestBody CompartitRequest compartitActualitzat) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CompartitResponse compartit = service.update(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())),
                uuid, compartitActualitzat);

        return ResponseEntity.ok(compartit);
    }

    @Operation(summary = "Elimina una entitat compartida per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entitat compartida eliminada"),
            @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete/admin/{uuid}")
    public ResponseEntity<Void> deleteCompartit(@PathVariable UUID uuid) {
        service.deleteByUuid(uuid);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Elimina una entitat compartida de l'usuari per UUID", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entitat compartida eliminada"),
            @ApiResponse(responseCode = "404", description = "Entitat compartida no trobada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @DeleteMapping("delete/{uuid}")
    public ResponseEntity<Void> deleteUserCompartit(@PathVariable UUID uuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        service.deleteUserCompartit(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), uuid);

        return ResponseEntity.ok().build();
    }

}
