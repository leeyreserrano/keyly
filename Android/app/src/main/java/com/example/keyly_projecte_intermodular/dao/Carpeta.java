package com.example.keyly_projecte_intermodular.dao;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Carpeta implements Serializable {
    private UUID uuid;
    private Bagul bagul;
    private String nom;
    private boolean favorit;
    private String dataCreacio;
    private List<Item> items;

    public Carpeta(String nom, boolean favorit) {
        this.nom = nom;
        this.favorit = favorit;
    }
}
