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
    UUID uuid;
    Usuari usuariCreador;
    Usuari usuariReceptor;
    TipusEntitat tipusEntitat;
    Permisos permisos;
    Carpeta carpeta;
    Item item;
    String dataCreacio;
}
