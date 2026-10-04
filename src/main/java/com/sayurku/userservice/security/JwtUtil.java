package com.sayurku.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate token dari id, email & role.
    // userId ikut disimpan supaya nanti gateway bisa meneruskannya sebagai header X-User-Id.
    public String generateToken(UUID userId, String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("uid", userId.toString())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
//
//    Ambil Email dari token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

//    Ambil Role dari token
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Cek apakah token masih valid
    public Boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
