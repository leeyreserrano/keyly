package com.keyly.model.response;

public record LoginResponse(
        String token) {
    public LoginResponse(String token) {
        this.token = token;
    }
}
