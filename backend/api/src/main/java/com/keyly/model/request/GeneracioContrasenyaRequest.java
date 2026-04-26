package com.keyly.model.request;

public record GeneracioContrasenyaRequest(
        Integer longitud,
        Boolean may,
        Integer quantitatMay,
        Boolean numeros,
        Integer quantitatNumeros,
        Boolean caractersEspecials,
        Integer quantitatCaractersEspecials) {

    public GeneracioContrasenyaRequest {
        longitud = longitud != null ? longitud : 8;
        may = may != null ? may : false;
        quantitatMay = quantitatMay != null ? quantitatMay : 0;
        numeros = numeros != null ? numeros : false;
        quantitatNumeros = quantitatNumeros != null ? quantitatNumeros : 0;
        caractersEspecials = caractersEspecials != null ? caractersEspecials : false;
        quantitatCaractersEspecials = quantitatCaractersEspecials != null ? quantitatCaractersEspecials : 0;
    }
}