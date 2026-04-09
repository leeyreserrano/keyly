package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.UsuariRequest;
import com.keyly.model.response.UsuariResponse;
import com.keyly.service.UsuariService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/usuari")
@Tag(name = "Usuari Controller", description = "Operacions sobre usuaris")
public class UsuariController {

    @Autowired
    private UsuariService service;

    @Operation(summary = "Obté tots els usuaris", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("all/admin")
    public ResponseEntity<List<UsuariResponse>> getAllUsuaris() {
        return ResponseEntity.ok(service.getAllUsuaris());
    }

    @Operation(summary = "Crea un usuari", description = "ADMIN / CAP", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuari creat"),
            @ApiResponse(responseCode = "404", description = "Departament, sucursal o rol no trobats")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP')")
    @PostMapping("add/admin/cap")
    public ResponseEntity<UsuariResponse> addUsuari(@RequestBody UsuariRequest u) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean esCap = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CAP"));

        if (esAdmin) {
            UsuariResponse usuari = service.save(u);

            return ResponseEntity.status(HttpStatus.CREATED).body(usuari);
        } else if (esCap) {
            UsuariResponse usuari = service
                    .save(service.getUsuariEntityByUuid(UUID.fromString(authentication.getName())),
                            u);

            return ResponseEntity.status(HttpStatus.CREATED).body(usuari);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Operation(summary = "Actualitza un usuari per UUID", description = "ADMIN / CAP", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuari actualitzat"),
            @ApiResponse(responseCode = "404", description = "Usuari, departament, rol o sucursal no trobats")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP')")
    @PutMapping("update/admin/cap/{uuid}")
    public ResponseEntity<UsuariResponse> updateUsuari(@PathVariable UUID uuid,
            @RequestBody UsuariRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean esCap = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CAP"));

        if (esAdmin) {
            UsuariResponse response = service.update(uuid, request);

            return ResponseEntity.ok(response);
        } else if (esCap) {
            UsuariResponse response = service
                    .update(service.getUsuariEntityByUuid(
                            UUID.fromString(authentication.getName())), uuid, request);

            return ResponseEntity.ok(response);
        }

        UsuariResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un usuari per UUID", description = "ADMIN / CAP", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuari eliminat"),
            @ApiResponse(responseCode = "404", description = "Usuari no trobat"),
            @ApiResponse(responseCode = "409", description = "Falta de permissos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP')")
    @DeleteMapping("delete/admin/cap/{uuid}")
    public ResponseEntity<UsuariResponse> deleteUsuari(@PathVariable UUID uuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean esCap = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CAP"));

        if (esAdmin)
            return ResponseEntity.ok(service.deleteByUuid(uuid));
        else if (esCap)
            return ResponseEntity.ok(service
                    .deleteByUuid(service.getUsuariEntityByUuid(
                            UUID.fromString(authentication.getName())), uuid));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
    }

}
