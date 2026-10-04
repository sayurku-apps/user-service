package com.sayurku.userservice.repository;

import com.sayurku.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    // Daftar user per role untuk halaman admin, mis. semua STAFF
    Page<User> findByRole(User.Role role, Pageable pageable);
}
