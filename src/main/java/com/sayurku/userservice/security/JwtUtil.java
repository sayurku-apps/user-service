package com.sayurku.userservice.security;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

// user-service hanya MEMBUAT token (saat register/login).
// Yang memeriksa token adalah api-gateway, dengan secret yang sama.
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // userId ikut disimpan supaya gateway bisa meneruskannya sebagai header X-User-Id.
    // branchId hanya ada untuk STAFF (cabang tempatnya bekerja), diteruskan sebagai X-User-Branch-Id.
    public String generateToken(UUID userId, String email, String role, UUID branchId) {
        JwtBuilder builder = Jwts.builder()
                .subject(email)
                .claim("uid", userId.toString())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey());
        if (branchId != null) {
            builder.claim("branchId", branchId.toString());
        }
        return builder.compact();
    }
}
