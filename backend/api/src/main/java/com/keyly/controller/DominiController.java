package com.keyly.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.DominiRequest;
import com.keyly.model.response.DominiResponse;
import com.keyly.service.DominiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@Tag(name = "Domini Controller", description = "Operacions sobre dominis")
public class DominiController {

    @Autowired
    private DominiService service;

    @Operation(summary = "Obté tots els domini")
    @GetMapping("dominis")
    public ResponseEntity<List<DominiResponse>> getAllDominis() {
        return ResponseEntity.ok(service.getAllDominis());
    }

    @Operation(summary = "Obté un domini per UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Domini trobat"),
            @ApiResponse(responseCode = "404", description = "Domini no trobat")
    })
    @GetMapping("domini/{uuid}")
    public ResponseEntity<DominiResponse> getDomini(@PathVariable UUID uuid) {
        DominiResponse domini = service.getByUuid(uuid);

        return ResponseEntity.status(HttpStatus.CREATED).body(domini);
    }

    @Operation(summary = "Crea un domini")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Domini creat"),
            @ApiResponse(responseCode = "404", description = "Sucursal no trobada"),
            @ApiResponse(responseCode = "409", description = "El domini no es válid o ja existeix")
    })
    @PostMapping("domini")
    public ResponseEntity<DominiResponse> addDomini(@RequestBody DominiRequest d) {
        DominiResponse domini = service.save(d);

        return ResponseEntity.status(HttpStatus.CREATED).body(domini);
    }

    @Operation(summary = "Crea un llistat de dominis")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dominis creats"),
            @ApiResponse(responseCode = "404", description = "Una de les sucursals no trobades"),
            @ApiResponse(responseCode = "409", description = "Un dels dominis no és válid o ja existeix")
    })
    @PostMapping("dominis")
    public ResponseEntity<List<DominiResponse>> addDominis(@RequestBody List<DominiRequest> ds) {
        List<DominiResponse> responses = new ArrayList<>();
        for (DominiRequest d : ds) {
            responses.add(service.save(d));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Actualitza un domini per UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Domini creat"),
            @ApiResponse(responseCode = "404", description = "Sucursal no trobada")
    })
    @PutMapping("domini/{uuid}")
    public ResponseEntity<DominiResponse> updateDomini(@PathVariable UUID uuid, @RequestBody DominiRequest request) {
        DominiResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un domini per UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Domini eliminat"),
            @ApiResponse(responseCode = "404", description = "Domini no trobat")
    })
    @DeleteMapping("domini/{uuid}")
    public ResponseEntity<DominiResponse> deleteDomini(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Obté un domini per ID", deprecated = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Domini trobat"),
            @ApiResponse(responseCode = "404", description = "Domini no trobat")
    })
    @Deprecated
    @GetMapping("domini/id/{id}")
    public ResponseEntity<DominiResponse> getDomini(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Actualitza un domini per ID", deprecated = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Domini actualitzat"),
            @ApiResponse(responseCode = "404", description = "Domini no trobat")
    })
    @Deprecated
    @PutMapping("domini/id/{id}")
    public ResponseEntity<DominiResponse> updateDomini(@PathVariable Long id,
            @RequestBody DominiRequest dominiActualitzat) {
        DominiResponse domini = service.update(id, dominiActualitzat);

        return ResponseEntity.ok(domini);
    }

    @Operation(summary = "Elimina un domini per ID", deprecated = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Domini eliminat"),
            @ApiResponse(responseCode = "404", description = "Domini no trobat")
    })
    @Deprecated
    @DeleteMapping("domini/id/{id}")
    public ResponseEntity<DominiResponse> deleteDomini(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

}
