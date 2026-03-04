package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Item;
import com.keyly.service.ItemService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


// TODO - Gestionar las contraseñas seguras (Una vez el jwt esté hecho)
@RestController
public class ItemController {

    @Autowired
    private ItemService service;

    @GetMapping("items")
    public ResponseEntity<List<Item>> getAllItem() {
        return ResponseEntity.ok(service.getAllItems());
    }
    
    @GetMapping("item/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        Item item = service.getById(id);

        return ResponseEntity.ok(item);
    }

    @PostMapping("item")
    public ResponseEntity<Item> addItem(@RequestBody Item i) {
        Item item = service.save(i);

        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PostMapping("items")
    public ResponseEntity<List<Item>> addItems(@RequestBody List<Item> is) {
        for (Item i : is) {
            service.save(i);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(is);
    }

    @PutMapping("item/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item itemActualitzat) {
        Item item = service.update(itemActualitzat);

        return ResponseEntity.ok(item);
    }
    
    @DeleteMapping("item/{id}")
    public ResponseEntity<Item> deleteItem(@PathVariable Long id) {
        Item item = service.getById(id);

        service.delete(item);

        return ResponseEntity.ok(item);
    }

}
