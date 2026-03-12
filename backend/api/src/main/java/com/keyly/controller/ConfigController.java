package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.ConfigRequest;
import com.keyly.model.response.ConfigResponse;
import com.keyly.service.ConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@Tag(name = "Config Controller", description = "Operacions sobre la Configuracio d'una sucursal")
public class ConfigController {

    @Autowired
    private ConfigService service;

    @Operation(summary = "Obté totes les Config")
    @GetMapping("config")
    public ResponseEntity<List<ConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok(service.getConfigs());
    }

    @Operation(summary = "Obté una config per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Config trobada"),
        @ApiResponse(responseCode = "404", description = "Config no trobada")
    })
    @GetMapping("config/{uuid}")
    public ResponseEntity<ConfigResponse> getConfig(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getConfig(uuid));
    }

    @Operation(summary = "Obté una config per la UUID d'una sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Config trobada"),
        @ApiResponse(responseCode = "404", description = "Config o sucursal no trobada")
    })
    @GetMapping("config/sucursal/{uuid}")
    public ResponseEntity<ConfigResponse> getConfigBySucursalUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getConfigBySucursalUuid(uuid));
    }

    @Operation(summary = "Actualitza una config per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Config actualitzada"),
        @ApiResponse(responseCode = "404", description = "Config no actualitzada")
    })
    @PatchMapping("config/{uuid}")
    public ResponseEntity<ConfigResponse> updateConfig(@PathVariable UUID uuid, @RequestBody ConfigRequest c) {
        return ResponseEntity.ok(service.updateConfig(uuid, c));
    }

    @Operation(summary = "Obté una config per la UUID d'una sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Config trobada"),
        @ApiResponse(responseCode = "404", description = "Config o sucursal no trobada")
    })
    @PatchMapping("config/sucursal/{uuid}")
    public ResponseEntity<ConfigResponse> updateConfigBySucursalUuid(@PathVariable UUID uuid, @RequestBody ConfigRequest c) {
        return ResponseEntity.ok(service.updateConfigBySucursalUuid(uuid, c));
    }

}
