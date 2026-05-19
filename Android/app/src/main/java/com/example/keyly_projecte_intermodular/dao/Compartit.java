package com.example.keyly_projecte_intermodular.dao;

import com.example.keyly_projecte_intermodular.utils.TipusEntitat;
import com.example.keyly_projecte_intermodular.utils.Permisos;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Compartit implements Serializable {
    private UUID uuid;
    private Usuari usuariCreador;
    private Usuari usuariReceptor;
    private TipusEntitat tipusEntitat;
    private Permisos permisos;
    private Carpeta carpeta;
    private Item item;
    private String dataCreacio;
    private int comptadorAccess;
    private String ultimAccess;
}
