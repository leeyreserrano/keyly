package com.keyly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keyly.model.Usuari;
import com.keyly.model.request.AuthRequest;
import com.keyly.model.response.UsuariResponse;
import com.keyly.security.JwtUtils;

@Service
public class AuthService {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public boolean login(AuthRequest request) {
        Usuari usuari = usuariService.getUsuariEntityByMail(request.correu());

        if (passwordEncoder.matches(request.contrasenya(), usuari.getContrasenya())) {
            usuariService.actualitzarUltimLogin(usuari);

            return true;
        }

        return false;
    }

    public UsuariResponse getUsuari(String correu) {
        Usuari usuari = usuariService.getUsuariEntityByMail(correu);

        return new UsuariResponse(usuari);
    }

    public String generateToken(String correu) {
        Usuari usuari = usuariService.getUsuariEntityByMail(correu);
        return jwtUtils.generateToken(usuari);
    }

    public boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }

    public String getEmailFromToken(String token) {
        return jwtUtils.getEmailFromToken(token);
    }
}
