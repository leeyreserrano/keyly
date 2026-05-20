package com.example.keyly_projecte_intermodular.dao;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sucursal implements Serializable {
    private UUID uuid; // Només s'utilitza la uuid (es random), per identificar l'ítem
    private String nom;
    private String direccio;
    private String ciutat;
    private String pais;
    private String telefon;
    private String correu;
}
