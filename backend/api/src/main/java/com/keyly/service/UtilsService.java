package com.keyly.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.keyly.model.request.GeneracioContrasenyaRequest;
import com.keyly.model.response.GeneracioContrasenyaResponse;

@Service
public class UtilsService {

    public GeneracioContrasenyaResponse generacioContrasenyaPersonalitzada(GeneracioContrasenyaRequest request) {
        List<Character> passwordChars = new ArrayList<>();

        if (request.quantitatMay() + request.quantitatNumeros() + request.quantitatCaractersEspecials() > request.longitud()) {
            throw new IllegalArgumentException("La longitud total de los caracteres no puede ser mayor que la longitud de la contraseña.");
        }

        if (request.may()) {
            for (int i = 0; i < request.quantitatMay(); i++) {
                passwordChars.add((char) (Math.random() * 26 + 'A'));
            }
        }

        if (request.numeros()) {
            for (int i = 0; i < request.quantitatNumeros(); i++) {
                passwordChars.add((char) (Math.random() * 10 + '0'));
            }
        }

        if (request.caractersEspecials()) {
            String specialChars = "!@#$%^&*()_+-=[]{}|;':.<>/?";
            for (int i = 0; i < request.quantitatCaractersEspecials(); i++) {
                passwordChars.add(specialChars.charAt((int) (Math.random() * specialChars.length())));
            }
        }

        while (passwordChars.size() < request.longitud()) {
            passwordChars.add((char) (Math.random() * 26 + 'a'));
        }

        Collections.shuffle(passwordChars);

        StringBuilder shuffledPassword = new StringBuilder();

        for (char c : passwordChars) {
            shuffledPassword.append(c);
        }

        return new GeneracioContrasenyaResponse(shuffledPassword.toString());
    }

}
