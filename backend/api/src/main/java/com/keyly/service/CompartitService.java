package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Compartit;
import com.keyly.model.Usuari;
import com.keyly.repo.CompartitRepo;

@Service
public class CompartitService {

    @Autowired
    private CompartitRepo repo;

    @Autowired
    private UsuariService usuariService;

    public List<Compartit> getAllCompartits() {
        return repo.findAll();
    }

    public Compartit getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Compartit no trobat amb el id: " + id));
    }

    public Compartit save(Compartit c) {
        Usuari u = usuariService.getById(c.getUsuari().getId());

        c.setUsuari(u);

        repo.save(c);

        return getById(c.getId());
    }

    public void delete(Compartit c) {
        Compartit compartit = getById(c.getId());

        repo.deleteById(compartit.getId());
    }

}
