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

        if (domini == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(domini);
    }
    
    @PostMapping("domini")
    public ResponseEntity<Domini> addDomini(@RequestBody Domini d) {
        Domini domini = service.save(d);

        return ResponseEntity.status(HttpStatus.CREATED).body(domini);
    }

    @PutMapping("domini/{id}")
    public ResponseEntity<Domini> updateDomini(@PathVariable Long id, @RequestBody Domini dominiActualitzat) {
        Domini domini = service.getById(id);

        if (domini == null) {
            return ResponseEntity.notFound().build();
        }

        domini.setNom(dominiActualitzat.getNom());

        Domini dominiGuardat = service.save(domini);

        return ResponseEntity.ok(dominiGuardat);
    }

    @DeleteMapping("domini/{id}")
    public ResponseEntity<Domini> deleteDomini(@PathVariable Long id) {
        Domini domini = service.getById(id);

        if (domini == null) {
            return ResponseEntity.notFound().build();
        }

        service.delete(domini);

        return ResponseEntity.ok(domini);
    }
    

}
