package com.sayurku.userservice.dto;

import com.sayurku.userservice.entity.User;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// PATCH /api/users/{id}/role. branchId wajib untuk STAFF, harus kosong untuk role lain.
public record UpdateRoleRequest(
        @NotNull(message = "Role wajib diisi")
        User.Role role,

        UUID branchId
) {}
