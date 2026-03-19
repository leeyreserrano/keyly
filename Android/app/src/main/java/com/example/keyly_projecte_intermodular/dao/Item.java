package com.example.keyly_projecte_intermodular.resources;

import com.bumptech.glide.Glide;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {

    private UUID uuid; // Solo usar uuid (es random), para identificar el item
    //private UUID bagul_uuid;
    private String titol;
    private String nom_usuari;
    private String contrasenya;
    private String url;
    private String notes;
    private boolean favorit;
    private String data_creacio;
    private String data_editat;
    private String ultim_access;
    //private byte[] favicon;

}
