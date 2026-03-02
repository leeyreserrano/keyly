package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Bagul;
import com.keyly.model.Usuari;
import com.keyly.repo.BagulRepo;

@Service
public class BagulService {

    @Autowired
    private BagulRepo repo;

    @Autowired
    private UsuariService usuariService;

    public List<Bagul> getAllContrasenyes() {
        return repo.findAll();
    }

    public Bagul getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Bagul no trobat amb el id: " + id));
    }

    public Bagul save(Bagul b) {
        Usuari u = usuariService.getById(b.getPropietari().getId());

        b.setPropietari(u);

        repo.save(b);

        return getById(b.getId());
    }

    public void delete(Bagul b) {
        Bagul bagul = getById(b.getId());

        repo.deleteById(bagul.getId());
    }

}
