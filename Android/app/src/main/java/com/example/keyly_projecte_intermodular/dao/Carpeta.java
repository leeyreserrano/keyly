package com.example.keyly_projecte_intermodular.dao;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Carpeta {
    private UUID uuid;
    private Bagul bagul;
    private String nom;
    private boolean favorit;
    private String data_creacio;
    private List<Item> items;

}
