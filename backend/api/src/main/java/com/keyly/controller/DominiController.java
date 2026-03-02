package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Domini;
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
    public ResponseEntity<List<Domini>> getAllDominis() {
        return ResponseEntity.ok(service.getAllDominis());
    }

    @GetMapping("domini/{id}")
    public ResponseEntity<Domini> getDomini(@PathVariable Long id) {
        Domini domini = service.getById(id);

        return ResponseEntity.ok(domini);
    }

    @PostMapping("domini")
    public ResponseEntity<Domini> addDomini(@RequestBody Domini d) {
        Domini domini = service.save(d);

        return ResponseEntity.status(HttpStatus.CREATED).body(domini);
    }

    @PostMapping("dominis")
    public ResponseEntity<List<Domini>> addDominis(@RequestBody List<Domini> ds) {
        for (Domini d : ds) {
            service.save(d);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ds);
    }

    @PutMapping("domini/{id}")
    public ResponseEntity<Domini> updateDomini(@PathVariable Long id, @RequestBody Domini dominiActualitzat) {
        Domini domini = service.getById(id);

        domini.setDomini(dominiActualitzat.getDomini());

        Domini dominiGuardat = service.save(domini);

        return ResponseEntity.ok(dominiGuardat);
    }

    @DeleteMapping("domini/{id}")
    public ResponseEntity<Domini> deleteDomini(@PathVariable Long id) {
        Domini domini = service.getById(id);

        service.delete(domini);

        return ResponseEntity.ok(domini);
    }

}
