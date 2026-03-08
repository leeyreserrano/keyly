package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Item;
import com.keyly.model.request.ItemRequest;
import com.keyly.model.response.ItemResponse;
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
    public ResponseEntity<List<ItemResponse>> getAllItem() {
        return ResponseEntity.ok(service.getAllItems());
    }

    @GetMapping("item/{uuid}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable UUID uuid) {
        ItemResponse item = service.getByUuid(uuid);

        return ResponseEntity.ok(item);
    }

    @PostMapping("item")
    public ResponseEntity<ItemResponse> addItem(@RequestBody ItemRequest i) {
        ItemResponse item = service.save(i);

        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PostMapping("items")
    public ResponseEntity<List<ItemResponse>> addItems(@RequestBody List<ItemRequest> is) {
        List<ItemResponse> responses = is
                .stream()
                .map(item -> service.save(item))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PutMapping("item/{uuid}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable UUID uuid, @RequestBody ItemRequest request) {
        ItemResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("item/{uuid}")
    public ResponseEntity<ItemResponse> deleteItem(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    @GetMapping("item/id/{id}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Deprecated
    @PutMapping("item/id/{id}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @RequestBody ItemRequest itemActualitzat) {
        ItemResponse item = service.update(id, itemActualitzat);

        return ResponseEntity.ok(item);
    }

    @Deprecated
    @DeleteMapping("item/id/{id}")
    public ResponseEntity<Item> deleteItem(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.ok(null);
    }

}
