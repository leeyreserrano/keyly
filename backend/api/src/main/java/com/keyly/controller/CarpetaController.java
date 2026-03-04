package com.keyly.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Carpeta;
import com.keyly.model.Item;
import com.keyly.service.CarpetaService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class CarpetaController {

    @Autowired
    private CarpetaService service;

    @GetMapping("carpetes")
    public ResponseEntity<List<Carpeta>> getAllCarpetes() {
        return ResponseEntity.ok(service.getAllCarpetes());
    }
    
    @GetMapping("carpeta/{id}")
    public ResponseEntity<Carpeta> getCarpeta(@PathVariable Long id) {
        Carpeta carpeta = service.getById(id);

        return ResponseEntity.ok(carpeta);
    }

    @GetMapping("carpeta/{id}/item")
    public ResponseEntity<Set<Item>> getCarpetaItems(@PathVariable Long id) {
        Set<Item> items = service.getCarpetaItem(id);

        return ResponseEntity.ok(items);
    }
 
    @PostMapping("carpeta")
    public ResponseEntity<Carpeta> addCarpeta(@RequestBody Carpeta c) {
        Carpeta carpeta = service.save(c);

        return ResponseEntity.status(HttpStatus.CREATED).body(carpeta);
    }
    
    @PostMapping("carpetes")
    public ResponseEntity<List<Carpeta>> addCarpetes(@RequestBody List<Carpeta> cs) {
        for (Carpeta c : cs) {
            service.save(c);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(cs);
    }

    @PostMapping("carpeta/{carpetaId}/item")
    public ResponseEntity<Carpeta> addItemToCarpeta(@PathVariable Long carpetaId, @RequestBody Item item) {
        Carpeta c = service.saveItemToCarpeta(carpetaId, item);

        return ResponseEntity.ok(c);
    }
    

    @PutMapping("carpeta/{id}")
    public ResponseEntity<Carpeta> updateCarpeta(@PathVariable Long id, @RequestBody Carpeta carpetaActualitzada) {
        Carpeta carpeta = service.getById(id);

        carpeta.setBagul(carpetaActualitzada.getBagul());
        carpeta.setNom(carpetaActualitzada.getNom());

        Carpeta carpetaGuardada = service.save(carpeta);

        return ResponseEntity.ok(carpetaGuardada);
    }

    @DeleteMapping("carpeta/{id}")
    public ResponseEntity<Carpeta> deleteCarpeta(@PathVariable Long id) {
        Carpeta carpeta = service.getById(id);

        service.delete(carpeta);

        return ResponseEntity.ok(carpeta);
    }

    @DeleteMapping("carpeta/{id}/item")
    public ResponseEntity<HttpStatus> deleteItemInCarpeta(@PathVariable Long id, @RequestBody Item item) {
        service.deleteItemInCarpeta(id, item);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

}
