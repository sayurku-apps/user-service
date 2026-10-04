package com.sayurku.userservice.controller;

import com.sayurku.userservice.dto.UpdateRoleRequest;
import com.sayurku.userservice.dto.UserResponse;
import com.sayurku.userservice.entity.User;
import com.sayurku.userservice.service.UserAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// Khusus ADMIN (dijaga di SecurityConfig, mirip middleware 'role:admin' di route Laravel)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    // ?role=STAFF &page= &size= &sort=name,asc
    @GetMapping
    public PagedModel<UserResponse> findAll(@RequestParam(required = false) User.Role role, Pageable pageable) {
        return new PagedModel<>(userAdminService.findAll(role, pageable));
    }

    // {"role":"STAFF","branchId":"..."} atau {"role":"CUSTOMER"}
    @PatchMapping("/{id}/role")
    public UserResponse updateRole(Authentication auth, @PathVariable UUID id,
                                   @Valid @RequestBody UpdateRoleRequest request) {
        return userAdminService.updateRole(auth.getName(), id, request);
    }
}
