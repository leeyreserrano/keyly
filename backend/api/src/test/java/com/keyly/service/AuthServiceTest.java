package com.keyly.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.keyly.model.Usuari;
import com.keyly.model.request.AuthRequest;
import com.keyly.security.JwtUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuariService usuariService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService service;

    private Usuari usuari;
    private String email;
    private String password;

    @BeforeEach
    void setUp() {
        email = "test@test.com";
        password = "password123";
        usuari = new Usuari();
        usuari.setCorreu(email);
        usuari.setContrasenya("hashedPassword");
    }

    @Test
    void login_shouldReturnTrue_whenCredentialsAreValid() {
        AuthRequest request = new AuthRequest(email, password);
        when(usuariService.getUsuariEntityByMail(email)).thenReturn(usuari);
        when(passwordEncoder.matches(password, usuari.getContrasenya())).thenReturn(true);

        boolean result = service.login(request);

        assertTrue(result);
        verify(usuariService).getUsuariEntityByMail(email);
        verify(passwordEncoder).matches(password, usuari.getContrasenya());
    }

    @Test
    void login_shouldReturnFalse_whenPasswordIsInvalid() {
        AuthRequest request = new AuthRequest(email, password);
        when(usuariService.getUsuariEntityByMail(email)).thenReturn(usuari);
        when(passwordEncoder.matches(password, usuari.getContrasenya())).thenReturn(false);

        boolean result = service.login(request);

        assertFalse(result);
        verify(usuariService).getUsuariEntityByMail(email);
        verify(passwordEncoder).matches(password, usuari.getContrasenya());
    }

    @Test
    void generateToken_shouldReturnToken() {
        String expectedToken = "jwt-token-value";
        when(usuariService.getUsuariEntityByMail(email)).thenReturn(usuari);
        when(jwtUtils.generateToken(usuari)).thenReturn(expectedToken);

        String result = service.generateToken(email);

        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(usuariService).getUsuariEntityByMail(email);
        verify(jwtUtils).generateToken(usuari);
    }

    @Test
    void validateToken_shouldReturnTrue_whenTokenIsValid() {
        String token = "valid-token";
        when(jwtUtils.validateToken(token)).thenReturn(true);

        boolean result = service.validateToken(token);

        assertTrue(result);
        verify(jwtUtils).validateToken(token);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsInvalid() {
        String token = "invalid-token";
        when(jwtUtils.validateToken(token)).thenReturn(false);

        boolean result = service.validateToken(token);

        assertFalse(result);
        verify(jwtUtils).validateToken(token);
    }

    @Test
    void getEmailFromToken_shouldReturnEmail() {
        String token = "valid-token";
        when(jwtUtils.getEmailFromToken(token)).thenReturn(email);

        String result = service.getEmailFromToken(token);

        assertNotNull(result);
        assertEquals(email, result);
        verify(jwtUtils).getEmailFromToken(token);
    }
}
