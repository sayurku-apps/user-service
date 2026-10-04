package com.sayurku.userservice.controller;

import com.sayurku.userservice.dto.AddressRequest;
import com.sayurku.userservice.dto.AddressResponse;
import com.sayurku.userservice.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// "me" = pemilik alamat diambil dari identitas login (header gateway), bukan dari URL
@RestController
@RequestMapping("/api/users/me/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public List<AddressResponse> findAll(Authentication auth) {
        return addressService.findAll(auth.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse create(Authentication auth, @Valid @RequestBody AddressRequest request) {
        return addressService.create(auth.getName(), request);
    }

    @PutMapping("/{id}")
    public AddressResponse update(Authentication auth, @PathVariable UUID id,
                                  @Valid @RequestBody AddressRequest request) {
        return addressService.update(auth.getName(), id, request);
    }

    @PatchMapping("/{id}/default")
    public AddressResponse setDefault(Authentication auth, @PathVariable UUID id) {
        return addressService.setDefault(auth.getName(), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication auth, @PathVariable UUID id) {
        addressService.delete(auth.getName(), id);
    }
}
