package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keyly.model.Departament;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.Usuari;
import com.keyly.repo.DepartamentRepo;
import com.keyly.repo.RolRepo;
import com.keyly.repo.SucursalRepo;
import com.keyly.repo.UsuariRepo;

@Service
public class UsuariService {

    @Autowired
    private UsuariRepo repo;

    @Autowired
    private SucursalRepo sucursalRepo;

    @Autowired
    private DepartamentRepo departamentRepo;

    @Autowired
    private RolRepo rolRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuari> getAllUsuaris() {
        return repo.findAll();
    }

    public Usuari getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Usuari save(Usuari u) {
        Sucursal s = sucursalRepo.findById(u.getSucursal().getId()).orElse(null);
        Departament d = departamentRepo.findById(u.getDepartament().getId()).orElse(null);
        Rol r = rolRepo.findById(u.getRol().getId()).orElse(null);

        if (s == null || d == null || r == null) {
            return null;
        }

        if(repo.existsByCorreu(u.getCorreu())) {
            return null;
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
        repo.deleteById(u.getId());
    }

    public boolean login(Long id, String contrasenya) {
        Usuari u = getById(id);

        String contrasenyaBD = u.getContrasenya();

        return passwordEncoder.matches(contrasenya, contrasenyaBD);
    }

}
