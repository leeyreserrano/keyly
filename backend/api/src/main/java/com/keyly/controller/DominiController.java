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

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

/*
 TODO - Pensar en meter una opción para permitir cualquier dominio
 TODO - Validación del dominio, que no tenga espacios, minusculas, que sea un dominio, que pueda empezar 
 por arroba o no, en caso de no empezar por arroba tendrá que insertarlo el servidor
 TODO - Que los dominios no se puedan repetir y asignarle un error personalizado
*/

@RestController
public class DominiController {

    @Autowired
    private DominiService service;

    @GetMapping("dominis")
    public ResponseEntity<List<DominiResponse>> getAllDominis() {
        return ResponseEntity.ok(service.getAllDominis());
    }

    @GetMapping("domini/{uuid}")
    public ResponseEntity<DominiResponse> getDomini(@PathVariable UUID uuid) {
        DominiResponse domini = service.getByUuid(uuid);

        return ResponseEntity.status(HttpStatus.CREATED).body(domini);
    }

    @PostMapping("domini")
    public ResponseEntity<DominiResponse> addDomini(@RequestBody DominiRequest d) {
        DominiResponse domini = service.save(d);

        return ResponseEntity.status(HttpStatus.CREATED).body(domini);
    }

    @PostMapping("dominis")
    public ResponseEntity<List<DominiResponse>> addDominis(@RequestBody List<DominiRequest> ds) {
        List<DominiResponse> responses = new ArrayList<>();
        for (DominiRequest d : ds) {
            responses.add(service.save(d));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PutMapping("domini/{uuid}")
    public ResponseEntity<DominiResponse> updateDomini(@PathVariable UUID uuid, @RequestBody DominiRequest request) {
        DominiResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("domini/{uuid}")
    public ResponseEntity<DominiResponse> deleteDomini(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    @GetMapping("domini/id/{id}")
    public ResponseEntity<DominiResponse> getDomini(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Deprecated
    @PutMapping("domini/id/{id}")
    public ResponseEntity<DominiResponse> updateDomini(@PathVariable Long id,
            @RequestBody DominiRequest dominiActualitzat) {
        DominiResponse domini = service.update(id, dominiActualitzat);

        return ResponseEntity.ok(domini);
    }

    @Deprecated
    @DeleteMapping("domini/id/{id}")
    public ResponseEntity<DominiResponse> deleteDomini(@PathVariable Long id) {
        service.getById(id);

        return ResponseEntity.ok(null);
    }

}
