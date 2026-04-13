package com.example.keyly_projecte_intermodular.dao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
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
}
