package com.sayurku.userservice.controller;

import com.sayurku.userservice.dto.AddressResponse;
import com.sayurku.userservice.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// Jalur antar-service. Gateway tidak meneruskan /internal/**, jadi tidak bisa dipanggil dari luar.
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalController {

    private final AddressService addressService;

    // Alamat milik user ini. Alamat user lain = 404.
    @GetMapping("/{userId}/addresses/{addressId}")
    public AddressResponse findAddress(@PathVariable UUID userId, @PathVariable UUID addressId) {
        return addressService.findForUser(userId, addressId);
    }
}
