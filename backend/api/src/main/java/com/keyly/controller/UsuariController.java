package com.keyly.controller;

import java.util.ArrayList;
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
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Tag(name = "Usuari Controller", description = "Operacions sobre usuaris")
public class UsuariController {

    @Autowired
    private UsuariService service;

    @Operation(summary = "Obté tots els usuaris")
    @GetMapping("usuaris")
    public ResponseEntity<List<UsuariResponse>> getAllUsuaris() {
        return ResponseEntity.ok(service.getAllUsuaris());
    }

    @Operation(summary = "Obté un usuari per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuari trobat"),
        @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    })
    @GetMapping("usuari/{uuid}")
    public ResponseEntity<UsuariResponse> getUsuari(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }

    @Operation(summary = "Crea un usuari")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuari creat"),
        @ApiResponse(responseCode = "404", description = "Departament, sucursal o rol no trobats")
    })
    @PostMapping("usuari")
    public ResponseEntity<UsuariResponse> addUsuari(@RequestBody UsuariRequest u) {
        UsuariResponse usuari = service.save(u);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuari);
    }

    @Operation(summary = "Crea un llistat de usuaris")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuaris creats"),
        @ApiResponse(responseCode = "404", description = "Un dels departaments, sucursals o rols no trobats")
    })
    @PostMapping("usuaris")
    public ResponseEntity<List<UsuariResponse>> addUsuaris(@RequestBody List<UsuariRequest> us) {
        List<UsuariResponse> responses = new ArrayList<>();
        for (UsuariRequest u : us) {
            responses.add(service.save(u));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Actualitza un usuari per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuari actualitzat"),
        @ApiResponse(responseCode = "404", description = "Usuari, departament, rol o sucursal no trobats")
    })
    @PutMapping("usuari/{uuid}")
    public ResponseEntity<UsuariResponse> updateUsuari(@PathVariable UUID uuid, @RequestBody UsuariRequest request) {
        UsuariResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un usuari per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuari eliminat"),
        @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    })
    @DeleteMapping("usuari/{uuid}")
    public ResponseEntity<UsuariResponse> deleteUsuari(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    // [x] Login provisional para ir probando
    @Operation(summary = "Login per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login permés"),
        @ApiResponse(responseCode = "404", description = "Login no permés")
    })
    @GetMapping("login/{uuid}")
    public ResponseEntity<Boolean> loginProvisional(@PathVariable UUID uuid, @RequestParam String contrasenya) {
        boolean faLogin = service.login(uuid, contrasenya);

        if (faLogin) {
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.notFound().build();
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Obté un usuari per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuari trobat"),
        @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    })
    @Deprecated
    @GetMapping("usuari/id/{id}")
    public ResponseEntity<UsuariResponse> getUsuari(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Actualitza un usuari per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuari actualitzat"),
        @ApiResponse(responseCode = "404", description = "Usuari, departament, rol o sucursal no trobats")
    })
    @Deprecated
    @PutMapping("usuari/id/{id}")
    public ResponseEntity<UsuariResponse> updateUsuari(@PathVariable Long id,
            @RequestBody UsuariRequest usuariActualitzat) {
        UsuariResponse response = service.update(id, usuariActualitzat);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un usuari per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuari eliminat"),
        @ApiResponse(responseCode = "404", description = "Usuari no trobat")
    })
    @Deprecated
    @DeleteMapping("usuari/id/{id}")
    public ResponseEntity<UsuariResponse> deleteUsuari(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

    // [x] Login provisional para ir probando
    @Operation(summary = "Login per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login permés"),
        @ApiResponse(responseCode = "404", description = "Login no permés")
    })
    @Deprecated
    @GetMapping("login/id/{id}")
    public ResponseEntity<Boolean> loginProvisional(@PathVariable Long id, @RequestParam String contrasenya) {
        boolean faLogin = service.login(id, contrasenya);

        if (faLogin) {
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.notFound().build();
    }

}
