package com.keyly.model.request;

public record GeneracioContrasenyaRequest(
        int longitud,
        boolean may,
        int quantitatMay,
        boolean numeros,
        int quantitatNumeros,
        boolean caractersEspecials,
        int quantitatCaractersEspecials) {

}
