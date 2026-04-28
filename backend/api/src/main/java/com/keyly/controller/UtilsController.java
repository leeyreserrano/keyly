package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.GeneracioContrasenyaRequest;
import com.keyly.model.response.GeneracioContrasenyaResponse;
import com.keyly.model.response.PawnedPasswordResponse;
import com.keyly.service.UtilsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/utils")
@Tag(name = "Utils Controller", description = "Operacions diverses")
public class UtilsController {

    @Autowired
    private UtilsService service;

    @Operation(summary = "Genera una contrasenya personalitzada", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PostMapping("custom/password")
    public ResponseEntity<GeneracioContrasenyaResponse> generacioContrasenyaPersonalitzada(
            @RequestBody GeneracioContrasenyaRequest request) {
        return ResponseEntity.ok(service.generacioContrasenyaPersonalitzada(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PostMapping("pwned/password/{prefix}/{suffix}")
    public ResponseEntity<List<PawnedPasswordResponse>> pawnedPasswords(@PathVariable String prefix, @PathVariable String suffix) {
        return ResponseEntity.ok(service.pawnedPassword(prefix, suffix));
    }

}
