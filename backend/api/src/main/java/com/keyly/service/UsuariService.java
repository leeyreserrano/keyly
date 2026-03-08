package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keyly.exception.CorreuExistentException;
import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Departament;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.Usuari;
import com.keyly.model.request.UsuariRequest;
import com.keyly.model.response.UsuariResponse;
import com.keyly.repo.UsuariRepo;

@Service
public class UsuariService {

    @Autowired
    private UsuariRepo repo;

    @Autowired
    private SucursalService sucursalService;

    @Autowired
    private DepartamentService departamentService;

    @Autowired
    private RolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuariResponse> getAllUsuaris() {
        return repo.findAll()
                .stream()
                .map(usuari -> new UsuariResponse(usuari))
                .toList();
    }

    public UsuariResponse getByUuid(UUID uuid) {
        Usuari usuari = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el uuid: " + uuid));

        return new UsuariResponse(usuari);
    }

    public Usuari getEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el uuid: " + uuid));
    }

    public UsuariResponse save(UsuariRequest u) {
        Sucursal s = new Sucursal(sucursalService.getByUuid(u.sucursalUuid()));
        Departament d = new Departament(s, departamentService.getByUuid(u.departamentUuid()));
        Rol r = new Rol(s, rolService.getByUuid(u.rolUuid()));

        if (repo.existsByCorreu(u.correu())) {
            throw new CorreuExistentException("El correu: " + u.correu() + " ja existeix.");
        }

        Usuari usuari = new Usuari(s, d, r, u);

        String contrasenyaCruda = u.contrasenya();
        String contrasenyaEncriptada = passwordEncoder.encode(contrasenyaCruda);
        usuari.setContrasenya(contrasenyaEncriptada);

        Usuari usuariGuardat = repo.save(usuari);

        return getByUuid(usuariGuardat.getUuid());
    }

    public UsuariResponse update(UUID uuid, UsuariRequest request) {
        Sucursal s = new Sucursal(sucursalService.getByUuid(request.sucursalUuid()));
        Departament d = new Departament(s, departamentService.getByUuid(request.departamentUuid()));
        Rol r = new Rol(s, rolService.getByUuid(request.rolUuid()));

        Usuari usuariGuardat = getEntityByUuid(uuid);

        usuariGuardat.setSucursal(s);
        usuariGuardat.setDepartament(d);
        usuariGuardat.setRol(r);
        usuariGuardat.setNom(request.nom());
        usuariGuardat.setCorreu(request.correu());
        usuariGuardat.setImatge(request.imatge());
        usuariGuardat.setPotAdministrar(request.potAdministrar());

        String contrasenyaCruda = request.contrasenya();
        String contrasenyaEncriptada = passwordEncoder.encode(contrasenyaCruda);
        usuariGuardat.setContrasenya(contrasenyaEncriptada);

        return new UsuariResponse(repo.save(usuariGuardat));
    }

    public UsuariResponse deleteByUuid(UUID uuid) {
        return new UsuariResponse(repo.deleteByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el uuid: " + uuid)));
    }

    public boolean login(UUID uuid, String contrasenya) {
        Usuari u = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el id: " + uuid));

        String contrasenyaBD = u.getContrasenya();

        return passwordEncoder.matches(contrasenya, contrasenyaBD);
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public UsuariResponse getById(Long id) {

        return new UsuariResponse(repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el id: " + id)));
    }

    @Deprecated
    public UsuariResponse update(Long id, UsuariRequest request) {
        Sucursal s = new Sucursal(sucursalService.getByUuid(request.sucursalUuid()));
        Departament d = new Departament(s, departamentService.getByUuid(request.departamentUuid()));
        Rol r = new Rol(s, rolService.getByUuid(request.rolUuid()));

        Usuari usuari = new Usuari(s, d, r, getById(id));

        return new UsuariResponse(repo.save(usuari));
    }

    @Deprecated
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Deprecated
    public boolean login(Long id, String contrasenya) {
        Usuari u = repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el id: " + id));

        String contrasenyaBD = u.getContrasenya();

        return passwordEncoder.matches(contrasenya, contrasenyaBD);
    }

}
