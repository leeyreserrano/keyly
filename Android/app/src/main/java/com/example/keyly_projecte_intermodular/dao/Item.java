package com.example.keyly_projecte_intermodular.dao;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item implements Serializable {
    private UUID uuid;
    private Bagul bagul;
    private String titol;
    private String nomUsuari;
    private String contrasenya;
    private String url;
    private String notes;
    private boolean favorit;
    private String dataCreacio;
    private String dataEditat;
    private String ultimAccess;

    public Item (String titol, String nomUsuari, String contrasenya, String url, String notes, boolean favorit) {
        this.titol = titol;
        this.nomUsuari = nomUsuari;
        this.contrasenya = contrasenya;
        this.url = url;
        this.notes = notes;
        this.favorit = favorit;
    }
}
