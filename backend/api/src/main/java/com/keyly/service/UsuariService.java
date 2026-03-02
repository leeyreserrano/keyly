package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keyly.exception.CorreuExistentException;
import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Departament;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.Usuari;
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

    public List<Usuari> getAllUsuaris() {
        return repo.findAll();
    }

    public Usuari getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el id: " + id));
    }

    public Usuari save(Usuari u) {
        Sucursal s = sucursalService.getById(u.getSucursal().getId());
        Departament d = departamentService.getById(u.getDepartament().getId());
        Rol r = rolService.getById(u.getRol().getId());

        if(repo.existsByCorreu(u.getCorreu())) {
            throw new CorreuExistentException("El correu: " + u.getCorreu() + " ja existeix.");
        }

        u.setSucursal(s);
        u.setDepartament(d);
        u.setRol(r);

        String contrasenyaCruda = u.getContrasenya();
        String contrasenyaEncriptada = passwordEncoder.encode(contrasenyaCruda);
        u.setContrasenya(contrasenyaEncriptada);

        repo.save(u);

        return getById(u.getId());
    }

    public void delete(Usuari u) {
        Usuari usuari = getById(u.getId());

        repo.deleteById(usuari.getId());
    }

    public boolean login(Long id, String contrasenya) {
        Usuari u = getById(id);

        String contrasenyaBD = u.getContrasenya();

        return passwordEncoder.matches(contrasenya, contrasenyaBD);
    }

}
