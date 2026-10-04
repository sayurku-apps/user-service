package com.sayurku.userservice.repository;

import com.sayurku.userservice.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    // Alamat utama paling atas, sisanya urut dari yang paling lama
    List<UserAddress> findByUserIdOrderByIsDefaultDescCreatedAtAsc(UUID userId);

    // Selalu cari pakai id + pemiliknya, jadi alamat user lain nggak akan ketemu
    Optional<UserAddress> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserId(UUID userId);
}
