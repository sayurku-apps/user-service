package com.sayurku.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

// Penjaga jalur antar-service /internal/**. Pemanggilnya (service lain) wajib mengirim header
// X-Internal-Token berisi rahasia bersama. Gateway memang tidak merutekan /internal, tapi tanpa
// penjaga ini siapa pun yang bisa menjangkau port service (mis. saat jalan lokal) bisa memanggilnya.
@Component
public class InternalTokenFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Internal-Token";

    private final byte[] expected;

    public InternalTokenFilter(@Value("${internal.token:}") String token) {
        if (token.isBlank()) {
            throw new IllegalStateException("internal.token belum diisi (application-local.properties / env INTERNAL_TOKEN)");
        }
        this.expected = token.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String given = request.getHeader(HEADER);
        // Dibandingkan dengan waktu konstan, supaya isi token tidak bisa ditebak dari lama respons
        if (given == null || !MessageDigest.isEqual(expected, given.getBytes(StandardCharsets.UTF_8))) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("""
                    {"timestamp":"%s","status":401,"message":"Token internal tidak valid"}""".formatted(LocalDateTime.now()));
            return;
        }
        chain.doFilter(request, response);
    }
}
