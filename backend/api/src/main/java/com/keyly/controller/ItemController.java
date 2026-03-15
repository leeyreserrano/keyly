package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.ItemRequest;
import com.keyly.model.response.ItemResponse;
import com.keyly.service.ItemService;

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
@Tag(name = "Item Controller", description = "Operacions sobre items")
public class ItemController {

    @Autowired
    private ItemService service;

    @Operation(summary = "Obté tots els items")
    @GetMapping("items")
    public ResponseEntity<List<ItemResponse>> getAllItem() {
        return ResponseEntity.ok(service.getAllItems());
    }

    @Operation(summary = "Obté un item per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item trobat"),
        @ApiResponse(responseCode = "404", description = "Item no trobat")
    })
    @GetMapping("item/{uuid}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable UUID uuid) {
        ItemResponse item = service.getByUuid(uuid);

        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Crea un item")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Item creat"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @PostMapping("item")
    public ResponseEntity<ItemResponse> addItem(@RequestBody ItemRequest i) {
        ItemResponse item = service.save(i);

        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @Operation(summary = "Crea un llistat de items")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Items creats"),
        @ApiResponse(responseCode = "404", description = "Un dels baguls no trobats")
    })
    @PostMapping("items")
    public ResponseEntity<List<ItemResponse>> addItems(@RequestBody List<ItemRequest> is) {
        List<ItemResponse> responses = is
                .stream()
                .map(item -> service.save(item))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Actualitza un item per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item actualitzat"),
        @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @PutMapping("item/{uuid}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable UUID uuid, @RequestBody ItemRequest request) {
        ItemResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Elimina un item per UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item eliminat"),
        @ApiResponse(responseCode = "404", description = "Item no trobat")
    })
    @DeleteMapping("item/{uuid}")
    public ResponseEntity<ItemResponse> deleteItem(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Operation(summary = "Obté un item per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item trobat"),
        @ApiResponse(responseCode = "404", description = "Item no trobat")
    })
    @Deprecated
    @GetMapping("item/id/{id}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Actualitza un item per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item actualitzat"),
        @ApiResponse(responseCode = "404", description = "Item no trobat")
    })
    @Deprecated
    @PutMapping("item/id/{id}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @RequestBody ItemRequest itemActualitzat) {
        ItemResponse item = service.update(id, itemActualitzat);

        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Elimina un item per ID", deprecated = true)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item eliminat"),
        @ApiResponse(responseCode = "404", description = "Item no trobat")
    })
    @Deprecated
    @DeleteMapping("item/id/{id}")
    public ResponseEntity<ItemResponse> deleteItem(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

}
