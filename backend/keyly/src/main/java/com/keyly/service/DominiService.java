package com.keyly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return repo.findById(id).orElse(null);
    }

    public Domini save(Domini d) {
        repo.save(d);

        return getById(d.getId());
    }

    public void delete(Domini d) {
        repo.deleteById(d.getId());
    }

}
