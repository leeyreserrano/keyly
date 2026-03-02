package com.keyly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.Usuari;
import com.keyly.service.UsuariService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
    TODO - Validación del mail, sin espacios, en minuscula, que sea un mail, que cumpla con la tabla de dominios
    TODO - Al cambiar la contrasenya, se deberá de volver a encriptar todas las contraseñas del usuario en la bd con la nueva contraseña
    TODO - Manejar las imagenes de los usuarios
*/

@RestController
public class UsuariController {

    @Autowired
    private UsuariService service;

    @GetMapping("usuaris")
    public ResponseEntity<List<Usuari>> getAllUsuaris() {
        return ResponseEntity.ok(service.getAllUsuaris());
    }

    @GetMapping("usuari/{id}")
    public ResponseEntity<Usuari> getUsuari(@PathVariable Long id) {
        Usuari usuari = service.getById(id);

        return ResponseEntity.ok(usuari);
    }

    @PostMapping("usuari")
    public ResponseEntity<Usuari> addUsuari(@RequestBody Usuari u) {
        Usuari usuari = service.save(u);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuari);
    }

    @PostMapping("usuaris")
    public ResponseEntity<List<Usuari>> addUsuaris(@RequestBody List<Usuari> us) {
        for (Usuari u : us) {
            service.save(u);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(us);
    }

    @PutMapping("usuari/{id}")
    public ResponseEntity<Usuari> updateUsuari(@PathVariable Long id, @RequestBody Usuari usuariActualitzat) {
        Usuari usuari = service.getById(id);

        usuari.setSucursal(usuariActualitzat.getSucursal());
        usuari.setDepartament(usuariActualitzat.getDepartament());
        usuari.setRol(usuariActualitzat.getRol());
        usuari.setNom(usuariActualitzat.getNom());
        usuari.setCorreu(usuariActualitzat.getCorreu());
        usuari.setContrasenya(usuariActualitzat.getContrasenya());
        usuari.setPotAdministrar(usuariActualitzat.getPotAdministrar());

        Usuari usuariGuardat = service.save(usuari);

        return ResponseEntity.ok(usuariGuardat);
    }

    @DeleteMapping("usuari/{id}")
    public ResponseEntity<Usuari> deleteUsuari(@PathVariable Long id) {
        Usuari usuari = service.getById(id);

        service.delete(usuari);

        return ResponseEntity.ok(usuari);
    }

    // [x] Login provisional para ir probando
    @GetMapping("login/{id}")
    public ResponseEntity<Boolean> loginProvisional(@PathVariable Long id, @RequestParam String contrasenya) {
        boolean faLogin = service.login(id, contrasenya);

        if (faLogin) {
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.notFound().build();
    }

}
