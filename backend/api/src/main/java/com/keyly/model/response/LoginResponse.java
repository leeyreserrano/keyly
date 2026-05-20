package com.keyly.model.response;

public record LoginResponse(
        String token,
        UsuariResponse usuari,
        byte[] kdfSalt,
        String encryptedPrivateKey) {
    public LoginResponse(String token, UsuariResponse usuari, byte[] kdfSalt, String encryptedPrivateKey) {
        this.token = token;
        this.usuari = usuari;
        this.kdfSalt = kdfSalt;
        this.encryptedPrivateKey = encryptedPrivateKey;
    }
}
