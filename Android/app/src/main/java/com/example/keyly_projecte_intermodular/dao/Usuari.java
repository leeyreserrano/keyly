package com.example.keyly_projecte_intermodular.DAO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private UUID uuid; // Solo usar uuid (es random), para identificar el item
    private Sucursal sucursal;
    private Departament departament;
    private Rol rol;
    private String nom;
    private String correu;
    private String imatge;
    private String dataCreacio;
    private String ultimLogin;
    private boolean potAdministrar;
}
