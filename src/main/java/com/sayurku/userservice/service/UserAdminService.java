package com.sayurku.userservice.service;

import com.sayurku.userservice.client.BranchClient;
import com.sayurku.userservice.dto.UpdateRoleRequest;
import com.sayurku.userservice.dto.UserResponse;
import com.sayurku.userservice.entity.User;
import com.sayurku.userservice.exception.InvalidRequestException;
import com.sayurku.userservice.exception.ResourceNotFoundException;
import com.sayurku.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// Kelola user oleh ADMIN. Siapa yang boleh memanggil diatur di SecurityConfig.
@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final BranchClient branchClient;

    /** Semua user, bisa difilter role (?role=STAFF) */
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(User.Role role, Pageable pageable) {
        Page<User> users = role == null ? userRepository.findAll(pageable) : userRepository.findByRole(role, pageable);
        return users.map(UserResponse::from);
    }

    /**
     * Ubah role, mis. angkat jadi STAFF cabang tertentu.
     * Berlaku setelah user itu login ulang, karena role & cabang tersimpan di dalam token-nya.
     */
    @Transactional
    public UserResponse updateRole(String adminEmail, UUID userId, UpdateRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan: " + userId));

        // Supaya admin terakhir tidak sengaja mengunci dirinya sendiri
        if (user.getEmail().equals(adminEmail)) {
            throw new InvalidRequestException("Tidak bisa mengubah role akun sendiri");
        }

        if (request.role() == User.Role.STAFF) {
            if (request.branchId() == null) {
                throw new InvalidRequestException("STAFF wajib punya cabang (branchId)");
            }
            if (!branchClient.exists(request.branchId())) {
                throw new InvalidRequestException("Cabang tidak ditemukan: " + request.branchId());
            }
        } else if (request.branchId() != null) {
            throw new InvalidRequestException("Hanya STAFF yang punya cabang");
        }

        user.setRole(request.role());
        user.setBranchId(request.branchId());
        return UserResponse.from(user);   // tersimpan otomatis (dirty checking)
    }
}
