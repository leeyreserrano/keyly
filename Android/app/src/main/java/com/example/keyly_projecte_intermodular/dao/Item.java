package com.example.keyly_projecte_intermodular.dao;

import com.example.keyly_projecte_intermodular.utils.Elements;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item extends Elements implements Serializable {
    private UUID uuid;
    private String titol;
    private String nomUsuari;
    private String contrasenya;
    private String iv;
    private EncryptedDataKey encryptedDataKey;
    private String url;
    private String notes;
    private boolean favorit;
    private String dataCreacio;
    private String dataEditat;
    private String ultimAccess;
    private int comptadorAccess;
    private boolean dinsDeCarpeta;
    private Bagul bagul;

    public Item (String titol, String nomUsuari, String contrasenya, String iv, String url, String notes, boolean favorit) {
        this.titol = titol;
        this.nomUsuari = nomUsuari;
        this.contrasenya = contrasenya;
        this.url = url;
        this.notes = notes;
        this.favorit = favorit;
        this.iv = iv;
    }
}
