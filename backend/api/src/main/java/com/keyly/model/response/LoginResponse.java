package com.keyly.model.response;

public record LoginResponse(
        String token,
        UsuariResponse usuari) {
    public LoginResponse(String token, UsuariResponse usuari) {
        this.token = token;
        this.usuari = usuari;
    }
}
