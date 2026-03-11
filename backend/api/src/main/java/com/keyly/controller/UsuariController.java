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

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
    TODO - Al cambiar la contrasenya, se deberá de volver a encriptar todas las contraseñas del usuario en la bd con la nueva contraseña (JWT hecho)
    TODO - Manejar las imagenes de los usuarios
*/

@RestController
public class UsuariController {

    @Autowired
    private UsuariService service;

    @GetMapping("usuaris")
    public ResponseEntity<List<UsuariResponse>> getAllUsuaris() {
        return ResponseEntity.ok(service.getAllUsuaris());
    }

    @GetMapping("usuari/{uuid}")
    public ResponseEntity<UsuariResponse> getUsuari(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }

    @PostMapping("usuari")
    public ResponseEntity<UsuariResponse> addUsuari(@RequestBody UsuariRequest u) {
        UsuariResponse usuari = service.save(u);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuari);
    }

    @PostMapping("usuaris")
    public ResponseEntity<List<UsuariResponse>> addUsuaris(@RequestBody List<UsuariRequest> us) {
        List<UsuariResponse> responses = new ArrayList<>();
        for (UsuariRequest u : us) {
            responses.add(service.save(u));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PutMapping("usuari/{uuid}")
    public ResponseEntity<UsuariResponse> updateUsuari(@PathVariable UUID uuid, @RequestBody UsuariRequest request) {
        UsuariResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("usuari/{uuid}")
    public ResponseEntity<UsuariResponse> deleteUsuari(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    // [x] Login provisional para ir probando
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

    @Deprecated
    @GetMapping("usuari/id/{id}")
    public ResponseEntity<UsuariResponse> getUsuari(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Deprecated
    @PutMapping("usuari/id/{id}")
    public ResponseEntity<UsuariResponse> updateUsuari(@PathVariable Long id,
            @RequestBody UsuariRequest usuariActualitzat) {
        UsuariResponse response = service.update(id, usuariActualitzat);

        return ResponseEntity.ok(response);
    }

    @Deprecated
    @DeleteMapping("usuari/id/{id}")
    public ResponseEntity<UsuariResponse> deleteUsuari(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

    // [x] Login provisional para ir probando
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
