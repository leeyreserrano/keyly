package com.example.keyly_projecte_intermodular.dao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sucursal {
    private UUID uuid; // Solo usar uuid (es random), para identificar el item
    private String nom;
    private String direccio;
    private String ciutat;
    private String pais;
    private String telefon;
    private String correu;
}
