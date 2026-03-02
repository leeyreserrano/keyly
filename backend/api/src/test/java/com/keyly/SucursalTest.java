package com.keyly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.keyly.controller.SucursalController;
import com.keyly.model.Sucursal;
import com.keyly.service.SucursalService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class SucursalTest {

    /*
     POSTMAN TEST
      
      {
      "nom": "Prova",
      "direccio": "Carrer 4",
      "ciutat": "Barcelona",
      "pais": "España",
      "telefon": "+34 679664352",
      "correu": "prova@gmail.com"
      }
     
     */

    @Mock
    private SucursalService service;

    @InjectMocks
    private SucursalController controller;

    @Test
    void getAllSucursales() {
        List<Sucursal> sucursals = List.of(
                new Sucursal(1L, "Sucursal1", "Dir1", "BCN", "España", "123", "a@a.com"),
                new Sucursal(2L, "Sucursal2", "Dir2", "Madrid", "España", "456", "b@b.com"));

        when(service.getAllSucursals()).thenReturn(sucursals);

        ResponseEntity<List<Sucursal>> response = controller.getSucursals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Sucursal1", response.getBody().get(0).getNom());
    }

    @Test
    void getSucursal() {
        Sucursal s = new Sucursal(1L, "Sucursal1", "Dir1", "BCN", "España", "123", "a@a.com");

        when(service.getById(1L)).thenReturn(s);

        ResponseEntity<Sucursal> response = controller.getSucursal(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dir1", response.getBody().getDireccio());
    }

    @Test
    void addSucursal() {
        Sucursal s = new Sucursal(
                1L,
                "Prova",
                "Carrer madrid",
                "Barcelona",
                "Espanya",
                "+34 577983215",
                "prova@gmail.com");

        when(service.save(any())).thenReturn(s);

        Sucursal request = new Sucursal();
        request.setNom("Prova");

        ResponseEntity<Sucursal> response = controller.addSucursal(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Prova", response.getBody().getNom());
    }

    @Test
    void updateSucursal() {
        Long id = 1L;

        Sucursal existent = new Sucursal(
                id,
                "Antiga",
                "Dir",
                "BCN",
                "España",
                "123",
                "old@mail.com");

        Sucursal actualitzat = new Sucursal(
                null,
                "Nova",
                "Dir nova",
                "Madrid",
                "España",
                "999",
                "new@mail.com");

        when(service.getById(id)).thenReturn(existent);
        when(service.save(any(Sucursal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Sucursal> response = controller.updateSucursal(id, actualitzat);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nova", response.getBody().getNom());
        assertEquals("Madrid", response.getBody().getCiutat());
    }

    @Test
    void deleteSucursal() {
        Long id = 1L;

        Sucursal existent = new Sucursal(
                id,
                "Antiga",
                "Dir",
                "BCN",
                "España",
                "123",
                "old@mail.com");

        when(service.getById(id)).thenReturn(existent);

        ResponseEntity<Sucursal> response = controller.deleteSucursal(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
