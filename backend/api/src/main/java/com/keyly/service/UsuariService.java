package com.keyly.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keyly.exception.CorreuException;
import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.mapper.UsuariMapper;
import com.keyly.model.Departament;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.Usuari;
import com.keyly.model.request.UsuariRequest;
import com.keyly.model.response.ConfigResponse;
import com.keyly.model.response.DominiResponse;
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
    private ConfigService configService;

    @Autowired
    private DominiService dominiService;

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

        Usuari usuari = new Usuari(s, d, r, u);

        if (!correuValid(usuari))
            throw new CorreuException("El correu " + usuari.getCorreu() + " no es válid.");

        String contrasenyaCruda = u.contrasenya();
        String contrasenyaEncriptada = passwordEncoder.encode(contrasenyaCruda);
        usuari.setContrasenya(contrasenyaEncriptada);

        return new UsuariResponse(repo.save(usuari));
    }

    public UsuariResponse update(UUID uuid, UsuariRequest request) {
        Usuari usuari = getUsuariEntityByUuid(uuid);

        if (request.sucursalUuid() != null)
            usuari.setSucursal(sucursalService.getSucursalEntityByUuid(request.sucursalUuid()));
        if (request.departamentUuid() != null)
            usuari.setDepartament(departamentService.getDepartamentEntityByUuid(request.departamentUuid()));
        if (request.rolUuid() != null)
            usuari.setRol(rolService.getRolEntityByUuid(request.rolUuid()));

        mapper.updateUsuariFromDto(request, usuari);

        if (!correuValid(usuari))
            throw new CorreuException("El correu " + usuari.getCorreu() + " no es válid.");

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

    /**
     * Comprova que un correu no existeixi i que el seu domini estigui permés
     * 
     * @param correu Correu del usuari
     * @return Si es valid
     */
    public boolean correuValid(Usuari u) {
        if (repo.existsByCorreu(u.getCorreu()))
            throw new CorreuException("El correu: " + u.getCorreu() + " ja existeix.");

        ConfigResponse configResponse = configService.getConfigBySucursalUuid(u.getSucursal().getUuid());

        List<DominiResponse> dominiResponse = dominiService.getDominisBySucursalUuid(u.getSucursal().getUuid());

        String dominiCorreuUsuari = u.getCorreu().substring(u.getCorreu().indexOf('@'));

        if (!dominiService.esDominiValid(dominiCorreuUsuari)) {
            return false;
        }

        if (configResponse.permetreTotsDominis()) {
            return true;
        }

        for (DominiResponse domini : dominiResponse) {
            if (domini.domini().equals(dominiCorreuUsuari)) {
                return true;
            }
        }

        return false;
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
    public Usuari getUsuariEntityById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el id: " + id));
    }

    @Deprecated
    public UsuariResponse update(Long id, UsuariRequest request) {
        Usuari usuari = getUsuariEntityById(id);

        if (request.sucursalUuid() != null)
            usuari.setSucursal(sucursalService.getSucursalEntityByUuid(request.sucursalUuid()));
        if (request.departamentUuid() != null)
            usuari.setDepartament(departamentService.getDepartamentEntityByUuid(request.departamentUuid()));
        if (request.rolUuid() != null)
            usuari.setRol(rolService.getRolEntityByUuid(request.rolUuid()));

        mapper.updateUsuariFromDto(request, usuari);

        if (!correuValid(usuari))
            throw new CorreuException("El correu " + usuari.getCorreu() + " no es válid.");

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
