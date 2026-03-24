package com.keyly.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.keyly.model.Usuari;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Genera un Jason Web Token
     * 
     * Conté el rol intern del usuari i el seu correu.
     * 
     * @param usuari Usuari que vol el JWT
     * @return JWT
     */
    public String generateToken(Usuari usuari) {
        // Afegir propietats al claim per a que estiguin en el JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuari.getRolIntern());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(usuari.getUuid().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Verifica que el token hagi sigut firmat per el servidor
     * 
     * @param token JWT
     * @return Si el token está generat per el servidor
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey());
            return true;
        } catch (ExpiredJwtException ex) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Retorna el secret de JWT
     * 
     * @return JWT Secret
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Retorna el correu del usuari desde el token
     * 
     * @param token JWT
     * @return Correu del usari
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims == null ? null : claims.getSubject();
    }

    /**
     * Retorna el rol del usuari desde el token
     * 
     * @param token JWT
     * @return Rol del usuari
     */
    public String getRolFromToken(String token) {
        Claims claims = getClaims(token);
        return claims == null ? null : claims.get("rol").toString();
    }

    /**
     * Retorna les propietats (claims) del JWT
     * 
     * @param token JWT
     * @return Claims del JWT
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build().parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            return null;
        }
    }
}
