package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.ConfigRequest;
import com.keyly.model.response.ConfigResponse;
import com.keyly.service.ConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/config")
@Tag(name = "Config Controller", description = "Operacions sobre la configuració d'una sucursal")
public class ConfigController {

    @Autowired
    private ConfigService service;

    @Operation(summary = "Obté totes les Config", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("all/admin")
    public ResponseEntity<List<ConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok(service.getConfigs());
    }

    @Operation(summary = "Obté una config per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Config trobada"),
            @ApiResponse(responseCode = "404", description = "Config no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("get/admin/{uuid}")
    public ResponseEntity<ConfigResponse> getConfig(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getConfig(uuid));
    }

    @Operation(summary = "Obté una config per la UUID d'una sucursal", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Config trobada"),
            @ApiResponse(responseCode = "404", description = "Config o sucursal no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("get/admin/sucursal/{uuid}")
    public ResponseEntity<ConfigResponse> getConfigBySucursalUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getConfigBySucursalUuid(uuid));
    }

    @Operation(summary = "Actualitza una config per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Config actualitzada"),
            @ApiResponse(responseCode = "404", description = "Config no actualitzada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update/admin/{uuid}")
    public ResponseEntity<ConfigResponse> updateConfig(@PathVariable UUID uuid, @RequestBody ConfigRequest c) {
        return ResponseEntity.ok(service.updateConfig(uuid, c));
    }

    @Operation(summary = "Actualitza una config per la UUID d'una sucursal", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Config trobada"),
            @ApiResponse(responseCode = "404", description = "Config o sucursal no trobada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update/admin/sucursal/{uuid}")
    public ResponseEntity<ConfigResponse> updateConfigBySucursalUuid(@PathVariable UUID uuid,
            @RequestBody ConfigRequest c) {
        return ResponseEntity.ok(service.updateConfigBySucursalUuid(uuid, c));
    }

}
