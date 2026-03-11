package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.ConfigRequest;
import com.keyly.model.response.ConfigResponse;
import com.keyly.service.ConfigService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class ConfigController {

    @Autowired
    private ConfigService service;

    @GetMapping("config")
    public ResponseEntity<List<ConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok(service.getConfigs());
    }

    @GetMapping("config/{uuid}")
    public ResponseEntity<ConfigResponse> getConfig(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getConfig(uuid));
    }

    @GetMapping("config/sucursal/{uuid}")
    public ResponseEntity<ConfigResponse> getConfigBySucursalUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getConfigBySucursalUuid(uuid));
    }

    @PatchMapping("config/{uuid}")
    public ResponseEntity<ConfigResponse> updateConfig(@PathVariable UUID uuid, @RequestBody ConfigRequest c) {
        return ResponseEntity.ok(service.updateConfig(uuid, c));
    }

    @PatchMapping("config/sucursal/{uuid}")
    public ResponseEntity<ConfigResponse> updateConfigBySucursalUuid(@PathVariable UUID uuid, @RequestBody ConfigRequest c) {
        return ResponseEntity.ok(service.updateConfigBySucursalUuid(uuid, c));
    }

}
