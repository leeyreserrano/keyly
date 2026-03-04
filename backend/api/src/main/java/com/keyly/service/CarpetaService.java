package com.keyly.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keyly.exception.EntitatNoTrobadaException;
import com.keyly.model.Bagul;
import com.keyly.model.Carpeta;
import com.keyly.model.Item;
import com.keyly.repo.CarpetaRepo;

@Service
public class CarpetaService {

    @Autowired
    private CarpetaRepo repo;

    @Autowired
    private BagulService bagulService;

    @Autowired
    private ItemService itemService;

    public List<Carpeta> getAllCarpetes() {
        return repo.findAll();
    }

    public Carpeta getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntitatNoTrobadaException("Carpeta no trobada amb el id: " + id));
    }

    public Set<Item> getCarpetaItem(Long id) {
        Carpeta carpeta = getById(id);

        return carpeta.getItems();
    }

    public Carpeta save(Carpeta c) {
        Bagul b = bagulService.getById(c.getBagul().getId());

        c.setBagul(b);

        repo.save(c);

        return getById(c.getId());
    }

    public Carpeta saveItemToCarpeta(Long id, Item item) {
        Carpeta carpeta = getById(id);
        Item itemRecuperat = itemService.getById(item.getId());

        carpeta.addItem(itemRecuperat);
        repo.save(carpeta);

        return getById(carpeta.getId());
    }

    public void delete(Carpeta c) {
        Carpeta carpeta = getById(c.getId());

        repo.deleteById(carpeta.getId());
    }

    public void deleteItemInCarpeta(Long carpetaId, Item item) {
        Carpeta carpeta = getById(carpetaId);
        Item itemRecuperat = itemService.getById(item.getId());

        carpeta.removeItem(itemRecuperat);
        repo.save(carpeta);
    }

}
