package com.keyly.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.ItemRequest;
import com.keyly.model.response.ItemResponse;
import com.keyly.service.ItemService;
import com.keyly.service.UsuariService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Autowired
    private UsuariService usuariService;

    @Operation(summary = "Obté tots els items", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("items/all")
    public ResponseEntity<List<ItemResponse>> getAllItem() {
        return ResponseEntity.ok(service.getAllItems());
    }

    @Operation(summary = "Obté tots els items de l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("items")
    public ResponseEntity<List<ItemResponse>> getAllItemOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(service.getAllItemsByUsuariUuid(UUID.fromString(authentication.getName())));
    }

    @Operation(summary = "Obté un item per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item trobat"),
            @ApiResponse(responseCode = "404", description = "Item no trobat")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("item/admin/{uuid}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable UUID uuid) {
        ItemResponse item = service.getByUuid(uuid);

        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Obté un item per UUID", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item trobat"),
            @ApiResponse(responseCode = "404", description = "Item no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @GetMapping("item/{uuid}")
    public ResponseEntity<ItemResponse> getUserItem(@PathVariable UUID uuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ItemResponse response = service
                .getUserItem(usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), uuid);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crea un item a l'usuari", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item creat"),
            @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PostMapping("item")
    public ResponseEntity<ItemResponse> addItem(@RequestBody ItemRequest i) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ItemResponse item = service.save(usuariService.getByUuid(UUID.fromString(authentication.getName())), i);

        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @Operation(summary = "Actualitza un item per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item actualitzat"),
            @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("item/admin/{uuid}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable UUID uuid, @RequestBody ItemRequest request) {
        ItemResponse response = service.update(uuid, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualitza un item per UUID", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item actualitzat"),
            @ApiResponse(responseCode = "404", description = "Bagul no trobat")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @PutMapping("item/{uuid}")
    public ResponseEntity<ItemResponse> updateUsuariItem(@PathVariable UUID uuid, @RequestBody ItemRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ItemResponse item = service.update(usuariService.getByUuid(UUID.fromString(authentication.getName())),
                uuid, request);

        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Elimina un item per UUID", description = "ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item eliminat"),
            @ApiResponse(responseCode = "404", description = "Item no trobat")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("item/admin/{uuid}")
    public ResponseEntity<ItemResponse> deleteAnyItem(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.deleteByUuid(uuid));
    }

    @Operation(summary = "Elimina un item per UUID que l'usuari tingui", description = "ADMIN / CAP / USUARI", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item eliminat"),
            @ApiResponse(responseCode = "404", description = "Item no trobat"),
            @ApiResponse(responseCode = "409", description = "Falta de permissos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CAP', 'USUARI')")
    @DeleteMapping("item/{uuid}")
    public ResponseEntity<ItemResponse> deleteItem(@PathVariable UUID uuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(service.deleteByUuid(
                usuariService.getUsuariEntityByUuid(UUID.fromString(authentication.getName())), uuid));
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("item/id/{id}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Elimina un item per ID", deprecated = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item eliminat"),
            @ApiResponse(responseCode = "404", description = "Item no trobat")
    })
    @Deprecated
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("item/id/{id}")
    public ResponseEntity<ItemResponse> deleteItem(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

}
