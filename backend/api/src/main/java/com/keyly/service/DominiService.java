package com.keyly.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.CorreuExistentException;
import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Domini;
import com.keyly.repo.DominiRepo;

@Service
public class DominiService {

    @Autowired
    private DominiRepo repo;

    public List<Domini> getAllDominis() {
        return repo.findAll();
    }

    public Domini getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Domini no trobat amb el id: " + id));
    }

    public Domini save(Domini d) {
        if (!esDominiValid(d)) {
            throw new CorreuExistentException("El domini " + d.getDomini() + " no és un domini válid.");
        }

        repo.save(d);

        return getById(d.getId());
    }

    public void delete(Domini d) {
        Domini domini = getById(d.getId());

        repo.deleteById(domini.getId());
    }

    public boolean esDominiValid(Domini d) {
        final String EMAIL_REGEX = "^@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";

        return Pattern.matches(EMAIL_REGEX, d.getDomini());
    }

}
