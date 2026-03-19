package com.keyly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.keyly.model.request.AuthRequest;
import com.keyly.model.response.LoginResponse;
import com.keyly.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Autorització", description = "Servei per fer login i register")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest request) {
        boolean faLogin = service.login(request);

        if (faLogin)
            return ResponseEntity.ok(new LoginResponse(service.generateToken(request.correu())));

        return ResponseEntity.status(401).build();
    }

}
