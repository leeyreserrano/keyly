package com.keyly.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.keyly.exception.CorreuException;
import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.exception.ImageException;
import com.keyly.exception.UsuariException;
import com.keyly.mapper.UsuariMapper;
import com.keyly.model.Departament;
import com.keyly.model.Rol;
import com.keyly.model.Sucursal;
import com.keyly.model.Usuari;
import com.keyly.model.enums.RolIntern;
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

    private final Path root = Paths.get("/app/uploads/profile-pictures");

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

    public Usuari getUsuariEntityByMail(String mail) {
        return repo.findByCorreu(mail)
                .orElseThrow(() -> new EntitatNoTrobadaException("Usuari no trobat amb el correu: " + mail));
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

        usuari.setImatge("/uploads/profile-pictures/" + u.nom().toUpperCase().charAt(0) + ".svg");

        return new UsuariResponse(repo.save(usuari));
    }

    public UsuariResponse save(Usuari cap, UsuariRequest nouUsuari) {
        if (!cap.getPotAdministrar())
            throw new UsuariException("El cap " + cap.getNom() + " no pot crear usuaris.");

        Sucursal s = sucursalService.getSucursalEntityByUuid(cap.getSucursal().getUuid());
        Departament d = departamentService.getDepartamentEntityByUuid(cap.getDepartament().getUuid());
        Rol r = rolService.getRolEntityByUuid(cap.getRol().getUuid());

        Usuari usuari = new Usuari(s, d, r, nouUsuari);

        if (usuari.getRolIntern() != RolIntern.USUARI)
            throw new UsuariException("Un cap no pot crear a un usuari que no sigui USUARI");

        if (!correuValid(usuari))
            throw new CorreuException("El correu " + usuari.getCorreu() + " no es válid.");

        String contrasenyaCruda = nouUsuari.contrasenya();
        String contrasenyaEncriptada = passwordEncoder.encode(contrasenyaCruda);
        usuari.setContrasenya(contrasenyaEncriptada);

        usuari.setImatge("/uploads/profile-pictures/" + nouUsuari.nom().toUpperCase().charAt(0) + ".svg");

        return new UsuariResponse(repo.save(usuari));
    }

    public void saveImage(UUID uuid, MultipartFile file) {
        try {
            Files.createDirectories(root);

            String fileName = file.getOriginalFilename();
            Path destinationFile = root.resolve(fileName);

            Files.copy(file.getInputStream(), destinationFile);

            String ruta = "/uploads/profile-pictures/" + fileName;

            uploadImage(uuid, ruta);
        } catch (IOException e) {
            throw new ImageException("La imatge no s'ha pogut guardar.");
        }
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

    public UsuariResponse update(Usuari cap, UUID uuid, UsuariRequest request) {
        if (!cap.getPotAdministrar())
            throw new UsuariException("El cap " + cap.getNom() + " no pot actualitzar usuaris.");

        Usuari usuari = getUsuariEntityByUuid(uuid);

        if (!cap.getDepartament().getUuid().equals(usuari.getDepartament().getUuid()))
            throw new UsuariException("Un cap no pot actualitzar a usuaris d'un diferent departament.");

        if (usuari.getRolIntern() == RolIntern.ADMIN)
            throw new UsuariException("Un cap no pot actualitzar a un administrador");

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

    private void uploadImage(UUID requesterUuid, String urlImage) {
        Usuari usuari = getUsuariEntityByUuid(requesterUuid);

        usuari.setImatge(urlImage);

        repo.save(usuari);
    }

    public UsuariResponse deleteByUuid(UUID uuid) {
        UsuariResponse usuari = getByUuid(uuid);

        repo.deleteByUuid(uuid);

        return usuari;
    }

    public UsuariResponse deleteByUuid(Usuari cap, UUID uuid) {
        if (!cap.getPotAdministrar())
            throw new UsuariException("El cap " + cap.getNom() + " no pot eliminar usuaris.");

        UsuariResponse usuari = getByUuid(uuid);

        if (!cap.getDepartament().getUuid().equals(usuari.departament().uuid()))
            throw new UsuariException("Un cap no pot actualitzar a usuaris d'un diferent departament.");

        if (usuari.rolIntern() == RolIntern.ADMIN)
            throw new UsuariException("Un cap no pot actualitzar a un administrador");

        repo.deleteByUuid(uuid);

        return usuari;
    }

    public void actualitzarUltimLogin(Usuari u) {
        u.setDataUltimLogin(LocalDateTime.now());

        repo.save(u);
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

        if (configResponse.permetreTotsDominis())
            return true;

        List<DominiResponse> dominiResponse = dominiService.getDominisBySucursalUuid(u.getSucursal().getUuid());

        String dominiCorreuUsuari = u.getCorreu().substring(u.getCorreu().indexOf('@'));

        if (!dominiService.esDominiValid(dominiCorreuUsuari))
            return false;

        for (DominiResponse domini : dominiResponse) {
            if (domini.domini().equals(dominiCorreuUsuari))
                return true;
        }

        return false;
    }

}
