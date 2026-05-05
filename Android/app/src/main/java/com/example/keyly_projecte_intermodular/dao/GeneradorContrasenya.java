package com.example.keyly_projecte_intermodular.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeneradorContrasenya {
    private int longitud;
    private boolean may;
    private int quantitatMay;
    private boolean numeros;
    private int quantitatNumeros;
    private boolean caractersEspecials;
    private int quantitatCaractersEspecials;
}
