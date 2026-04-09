package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.BagulRequest;
import com.keyly.model.response.BagulResponse;
import com.keyly.service.BagulService;

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
@RequestMapping("/bagul")
@Tag(name = "Bagul Controller", description = "Operacions sobre baguls")
public class BagulController {

    @Autowired
    private BagulService service;

    @Operation(summary = "Obté tots els baguls", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("all/admin")
    public ResponseEntity<List<BagulResponse>> getAllBaguls() {
        return ResponseEntity.ok(service.getAllBaguls());
    }

    @Operation(summary = "Obté un bagul per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bagul trobat"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("get/admin/{uuid}")
    public ResponseEntity<BagulResponse> getBagul(@PathVariable UUID uuid) {
        BagulResponse bagul = service.getByUuid(uuid);

        return ResponseEntity.ok(bagul);
    }

    @Operation(summary = "Actualitza un bagul per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bagul actualitzat"),
        @ApiResponse(responseCode = "404", description = "Bagul o usuari no trobats")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update/admin/{uuid}")
    public ResponseEntity<BagulResponse> updateBagul(@PathVariable UUID uuid, @RequestBody BagulRequest request) {
        BagulResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

}
