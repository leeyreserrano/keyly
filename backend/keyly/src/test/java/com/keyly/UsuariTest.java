package com.keyly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.keyly.controller.UsuariController;
import com.keyly.model.Departament;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.Usuari;
import com.keyly.service.DepartamentService;
import com.keyly.service.RolService;
import com.keyly.service.SucursalService;
import com.keyly.service.UsuariService;

@ExtendWith(MockitoExtension.class)
public class UsuariTest {

    // TODO Terminar el test

    /*
     * {
     * "sucursal": {
     * "id": "2"
     * },
     * "departament": {
     * "id": "1"
     * },
     * "rol": {
     * "id": "2"
     * },
     * "nom": "Geri",
     * "correu": "c@gmail.com",
     * "contrasenya": "1234",
     * "potAdministrar": true
     * }
     */

    @Mock
    private UsuariService service;

    @Mock
    private SucursalService sucursalService;

    @Mock
    private DepartamentService departamentService;

    @Mock
    private RolService rolService;

    @InjectMocks
    private UsuariController controller;

    private List<Sucursal> sucursals = List.of(
            new Sucursal(1L, "Sucursal1", "Dir1", "BCN", "España", "123", "a@a.com"),
            new Sucursal(2L, "Sucursal2", "Dir2", "Madrid", "España", "456", "b@b.com"));

    private List<Departament> departaments = List.of(
            new Departament(1L, sucursals.get(0), "Dep1"),
            new Departament(2L, sucursals.get(1), "Dep2"));

    private List<Rol> rols = List.of(
            new Rol(1L, sucursals.get(0), "Rol1"),
            new Rol(2L, sucursals.get(1), "Rol2"));

    private List<Usuari> usuaris = new ArrayList<>(List.of(
            new Usuari(1L, sucursals.get(0), departaments.get(0),
                    rols.get(0), "Prova", "p@gmail.com", "",
                    "contrasenya_super_segura", null, null, false),
            new Usuari(2L, sucursals.get(0), departaments.get(0),
                    rols.get(0), "Prova2", "v@gmail.com", "",
                    "contrasenya_molt_segura", null, null, false)));

    @Test
    void getAllUsuaris() {
        when(service.getAllUsuaris()).thenReturn(usuaris);

        ResponseEntity<List<Usuari>> response = controller.getAllUsuaris();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("p@gmail.com", response.getBody().get(0).getCorreu());
        assertEquals("Prova2", response.getBody().get(1).getNom());
        assertEquals("Rol1", response.getBody().get(0).getRol().getNom());
    }

    @Test
    void getUsuari() {
        when(service.getById(1L)).thenReturn(usuaris.get(0));

        ResponseEntity<Usuari> response = controller.getUsuari(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("p@gmail.com", response.getBody().getCorreu());
        assertEquals("Sucursal1", response.getBody().getSucursal().getNom());
    }

    @Test
    void addRol() {
        when(service.save(any(Usuari.class))).thenAnswer(invocation -> {
            Usuari u = invocation.getArgument(0);
            u.setId((long) (usuaris.size() + 1));
            usuaris.add(u);
            return u;
        });

        Usuari u = new Usuari();
        u.setSucursal(sucursals.get(0));
        u.setDepartament(departaments.get(1));
        u.setRol(rols.get(0));
        u.setNom("NouUsuari");

        ResponseEntity<Usuari> response = controller.addUsuari(u);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Sucursal1", response.getBody().getSucursal().getNom());
        assertEquals(3, usuaris.size());
    }

    @Test
    void updateUsuari() {
        when(service.getById(1L)).thenReturn(usuaris.get(0));
        when(sucursalService.getById(1L)).thenReturn(sucursals.get(0));
        when(departamentService.getById(1L)).thenReturn(departaments.get(0));
        when(rolService.getById(1L)).thenReturn(rols.get(0));
        when(service.save(any(Usuari.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuari u = service.getById(1L);

        u.setNom("NouUsuari");

        ResponseEntity<Usuari> response = controller.updateUsuari(1L, u);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("NouUsuari", response.getBody().getNom());
        assertEquals("Dep1", response.getBody().getDepartament().getNom());

        response = controller.updateUsuari(4L, u);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUsuari() {
        when(service.getById(1L)).thenReturn(usuaris.get(0));

        ResponseEntity<Usuari> response = controller.deleteUsuari(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = controller.deleteUsuari(3L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void login() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String contrasenyaCruda = "1234";
        String hashReal = passwordEncoder.encode(contrasenyaCruda);

        Usuari usuari = new Usuari();
        usuari.setId(1L);
        usuari.setContrasenya(hashReal);

        when(service.login(eq(1L), eq(contrasenyaCruda)))
                .thenAnswer(invocation -> passwordEncoder.matches(contrasenyaCruda, hashReal));

        ResponseEntity<Boolean> response = controller.loginProvisional(1L, contrasenyaCruda);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

}
