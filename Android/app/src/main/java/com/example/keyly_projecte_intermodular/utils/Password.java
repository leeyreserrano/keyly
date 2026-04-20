package com.example.keyly_projecte_intermodular.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Password {
    private int longitud;
    private boolean may;
    private int quantitatMay;
    private boolean numeros;
    private int quantitatNumeros;
    private boolean caractersEspecials;
    private int quantitatCaractersEspecials;
}
