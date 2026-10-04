package com.sayurku.userservice.service;

import com.sayurku.userservice.dto.AuthResponse;
import com.sayurku.userservice.dto.LoginRequest;
import com.sayurku.userservice.dto.RegisterRequest;
import com.sayurku.userservice.dto.UserResponse;
import com.sayurku.userservice.entity.User;
import com.sayurku.userservice.exception.AccountDisabledException;
import com.sayurku.userservice.exception.DuplicateResourceException;
import com.sayurku.userservice.exception.InvalidCredentialsException;
import com.sayurku.userservice.exception.ResourceNotFoundException;
import com.sayurku.userservice.repository.UserRepository;
import com.sayurku.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request){
//        cek email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email sudah terdaftar");
        }

//        buat object user baru (loyaltyPoints & isActive pakai default dari entity)
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(User.Role.CUSTOMER)
                .build();

//        simpan ke database
        userRepository.save(user);

        return toAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Pesan sengaja disamakan untuk email & password salah,
        // supaya orang nggak bisa nebak email mana yang terdaftar.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email atau password salah"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Email atau password salah");
        }

        // Cek apakah akun aktif
        if (!user.getIsActive()) {
            throw new AccountDisabledException("Akun kamu telah dinonaktifkan");
        }

        return toAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    private AuthResponse toAuthResponse(User user) {
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
