package com.example.keyly_projecte_intermodular.dao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rol {
    private UUID uuid; // Solo usar uuid (es random), para identificar el item
    private Sucursal sucursal;
    private String nom;
}
