package com.example.keyly_projecte_intermodular.dao;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Departament implements Serializable {
    private UUID uuid; // Solo usar uuid (es random), para identificar el item
    private Sucursal sucursal;
    private String nom;
}
