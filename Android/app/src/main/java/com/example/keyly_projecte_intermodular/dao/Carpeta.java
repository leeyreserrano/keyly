package com.example.keyly_projecte_intermodular.dao;

import com.example.keyly_projecte_intermodular.utils.Elements;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Carpeta extends Elements implements Serializable {
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

    public Carpeta (UUID uuid, String nom) {
        this.uuid = uuid;
        this.nom = nom;
    }
}
