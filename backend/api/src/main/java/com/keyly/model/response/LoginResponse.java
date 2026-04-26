package com.keyly.model.response;

public record LoginResponse(
        String token,
        UsuariResponse usuari,
        byte[] kdfSalt) {
    public LoginResponse(String token, UsuariResponse usuari, byte[] kdfSalt) {
        this.token = token;
        this.usuari = usuari;
        this.kdfSalt = kdfSalt;
    }
}
