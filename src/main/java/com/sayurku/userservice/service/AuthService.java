package com.sayurku.userservice.service;

import com.sayurku.userservice.dto.AuthResponse;
import com.sayurku.userservice.dto.LoginRequest;
import com.sayurku.userservice.dto.RegisterRequest;
import com.sayurku.userservice.entity.User;
import com.sayurku.userservice.repository.UserRepository;
import com.sayurku.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request){
//        cek email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar");
        }

//        buat object user baru
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(User.Role.CUSTOMER)
                .loyaltyPoints(0)
                .isActive(true)
                .build();

//        simpan ke database
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Cari user berdasarkan email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email tidak ditemukan"));

        // Cek apakah akun aktif
        if (!user.getIsActive()) {
            throw new RuntimeException("Akun kamu telah dinonaktifkan");
        }

        // Cek password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Password salah");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
