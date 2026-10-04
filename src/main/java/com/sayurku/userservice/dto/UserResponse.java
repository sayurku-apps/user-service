package com.sayurku.userservice.dto;

import com.sayurku.userservice.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String role,
        UUID branchId,
        Integer loyaltyPoints,
        LocalDateTime createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getPhone(),
                u.getRole().name(),
                u.getBranchId(),
                u.getLoyaltyPoints(),
                u.getCreatedAt()
        );
    }
}
