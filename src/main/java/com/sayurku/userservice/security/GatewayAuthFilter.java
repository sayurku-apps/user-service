package com.sayurku.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Token sudah diperiksa api-gateway. Gateway lalu meneruskan identitasnya lewat header
// X-User-Email dan X-User-Role, jadi di sini cukup dibaca (sama seperti service lain).
// Header dari client selalu dibuang gateway, jadi tidak bisa dipalsukan dari luar.
@Component
public class GatewayAuthFilter extends OncePerRequestFilter {

    public static final String USER_EMAIL = "X-User-Email";
    public static final String USER_ROLE = "X-User-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String email = request.getHeader(USER_EMAIL);
        String role = request.getHeader(USER_ROLE);

        // Tanpa header = request publik (register/login) atau tidak lewat gateway.
        // Biarkan lanjut; SecurityConfig yang menolak kalau endpoint-nya butuh login.
        if (email != null && role != null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
