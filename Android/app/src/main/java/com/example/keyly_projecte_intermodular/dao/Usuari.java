package com.example.keyly_projecte_intermodular.dao;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Usuari implements Serializable {

    private UUID uuid; // Solo usar uuid (es random), para identificar el item
    private String nom;
    private String correu;
    private String kdfSalt;
    private String imatge;
    private String dataCreacio;
    private String ultimLogin;
    private boolean potAdministrar;
    private String rolIntern; // Rol app
    private Sucursal sucursal;
    private Departament departament;
    private Rol rol; // Puesto de trabajo

}
