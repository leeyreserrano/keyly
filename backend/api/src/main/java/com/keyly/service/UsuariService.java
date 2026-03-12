package com.keyly.service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keyly.exception.CorreuExistentException;
import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.UsuariMapper;
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

    @Autowired
    private UsuariMapper mapper;

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

    public Usuari getUsuariEntityByUuid(UUID uuid) {
        return repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el uuid: " + uuid));
    }

    public UsuariResponse save(UsuariRequest u) {
        Sucursal s = sucursalService.getSucursalEntityByUuid(u.sucursalUuid());
        Departament d = departamentService.getDepartamentEntityByUuid(u.departamentUuid());
        Rol r = rolService.getRolEntityByUuid(u.rolUuid());

        if (repo.existsByCorreu(u.correu()))
            throw new CorreuExistentException("El correu: " + u.correu() + " ja existeix.");

        if (correuValid(u.correu()) && u.correu() != null)
            throw new CorreuExistentException("El correu: " + u.correu() + " no és un correu valid.");

        Usuari usuari = new Usuari(s, d, r, u);

        String contrasenyaCruda = u.contrasenya();
        String contrasenyaEncriptada = passwordEncoder.encode(contrasenyaCruda);
        usuari.setContrasenya(contrasenyaEncriptada);

        return new UsuariResponse(repo.save(usuari));
    }

    public UsuariResponse update(UUID uuid, UsuariRequest request) {
        Sucursal s = null;
        Departament d = null;
        Rol r = null;

        if (correuValid(request.correu()) && request.correu() != null)
            throw new CorreuExistentException("El correu: " + request.correu() + " no és un correu valid.");

        if (request.sucursalUuid() != null)
            s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());
        if (request.departamentUuid() != null)
            d = departamentService.getDepartamentEntityByUuid(request.departamentUuid());
        if (request.rolUuid() != null)
            r = rolService.getRolEntityByUuid(request.rolUuid());

        Usuari usuari = getUsuariEntityByUuid(uuid);

        if (s != null)
            usuari.setSucursal(s);
        if (d != null)
            usuari.setDepartament(d);
        if (r != null)
            usuari.setRol(r);

        mapper.updateUsuariFromDto(request, usuari);

        return new UsuariResponse(repo.save(usuari));
    }

    public UsuariResponse deleteByUuid(UUID uuid) {
        UsuariResponse usuari = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return usuari;
    }

    public boolean login(UUID uuid, String contrasenya) {
        Usuari u = repo.findByUuid(uuid)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el id: " + uuid));

        String contrasenyaBD = u.getContrasenya();

        return passwordEncoder.matches(contrasenya, contrasenyaBD);
    }

    public boolean correuValid(String correu) {
        final String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)+$";

        return Pattern.matches(regex, correu);
    }

    /*
     * Métodos que desaparecerán en futuras versiones
     */

    @Deprecated
    public UsuariResponse getById(Long id) {
        return new UsuariResponse(repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el id: " + id)));
    }

    public Usuari getUsuariEntityById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el id: " + id));
    }

    @Deprecated
    public UsuariResponse update(Long id, UsuariRequest request) {
        Sucursal s = null;
        Departament d = null;
        Rol r = null;

        if (correuValid(request.correu()) && request.correu() != null)
            throw new CorreuExistentException("El correu: " + request.correu() + " no és un correu valid.");

        if (request.sucursalUuid() != null)
            s = sucursalService.getSucursalEntityByUuid(request.sucursalUuid());
        if (request.departamentUuid() != null)
            d = departamentService.getDepartamentEntityByUuid(request.departamentUuid());
        if (request.rolUuid() != null)
            r = rolService.getRolEntityByUuid(request.rolUuid());

        Usuari usuari = getUsuariEntityById(id);

        if (s != null)
            usuari.setSucursal(s);
        if (d != null)
            usuari.setDepartament(d);
        if (r != null)
            usuari.setRol(r);

        mapper.updateUsuariFromDto(request, usuari);

        return new UsuariResponse(repo.save(usuari));
    }

    @Deprecated
    public UsuariResponse deleteById(Long id) {
        UsuariResponse usuari = getById(id);

        repo.deleteById(id);

        return usuari;
    }

    @Deprecated
    public boolean login(Long id, String contrasenya) {
        Usuari u = repo.findById(id)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el id: " + id));

        String contrasenyaBD = u.getContrasenya();

        return passwordEncoder.matches(contrasenya, contrasenyaBD);
    }

}
