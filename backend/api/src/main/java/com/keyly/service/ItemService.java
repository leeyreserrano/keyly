package com.keyly.service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Bagul;
import com.keyly.model.Carpeta;
import com.keyly.model.Item;
import com.keyly.repo.ItemRepo;

@Service
public class ItemService {

    @Autowired
    private ItemRepo repo;

    @Autowired
    private BagulService bagulService;

    @Autowired
    @Lazy
    private CarpetaService carpetaService;

    public List<Item> getAllItems() {
        return repo.findAll();
    }

    public Item getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Item no trobat amb el id: " + id));
    }

    public Item save(Item i) {
        Bagul b = bagulService.getById(i.getBagul().getId());

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        i.setIv(iv);

        i.setBagul(b);

        if (i.getCarpetas() != null && !i.getCarpetas().isEmpty()) {
            Set<Carpeta> managed = new HashSet<>();

            for (Carpeta c : new HashSet<>(i.getCarpetas())) {
                Carpeta carpeta = carpetaService.getById(c.getId());
                managed.add(carpeta);
            }
            i.setCarpetas(managed);

            for (Carpeta carpeta : managed) {
                carpeta.getItems().add(i);
            }
        }

        repo.save(i);

        return getById(i.getId());
    }

    public Item update(Item itemActualitzat) {
        Item item = getById(itemActualitzat.getId());

        item.setBagul(itemActualitzat.getBagul());
        item.setTitol(itemActualitzat.getTitol());
        item.setNomUsuari(itemActualitzat.getNomUsuari());
        item.setContrasenya(itemActualitzat.getContrasenya());
        item.setIv(itemActualitzat.getIv());
        item.setUrl(itemActualitzat.getUrl());
        item.setNotes(itemActualitzat.getNotes());
        item.setFavorit(itemActualitzat.getFavorit());

        repo.save(item);

        return getById(item.getId());
    }

    public void delete(Item i) {
        Item item = getById(i.getId());

        repo.deleteById(item.getId());
    }

}
