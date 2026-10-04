package com.sayurku.userservice.service;

import com.sayurku.userservice.dto.AddressRequest;
import com.sayurku.userservice.dto.AddressResponse;
import com.sayurku.userservice.entity.User;
import com.sayurku.userservice.entity.UserAddress;
import com.sayurku.userservice.exception.ResourceNotFoundException;
import com.sayurku.userservice.repository.UserAddressRepository;
import com.sayurku.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserAddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> findAll(String email) {
        User user = getUser(email);
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtAsc(user.getId()).stream()
                .map(AddressResponse::from)
                .toList();
    }

    @Transactional
    public AddressResponse create(String email, AddressRequest request) {
        User user = getUser(email);

        // Alamat pertama otomatis jadi alamat utama
        boolean firstAddress = !addressRepository.existsByUserId(user.getId());

        UserAddress address = UserAddress.builder().user(user).build();
        applyRequest(address, request);
        addressRepository.save(address);

        if (firstAddress || Boolean.TRUE.equals(request.isDefault())) {
            makeDefault(user.getId(), address);
        }
        return AddressResponse.from(address);
    }

    @Transactional
    public AddressResponse update(String email, UUID id, AddressRequest request) {
        User user = getUser(email);
        UserAddress address = getOwnedAddress(id, user.getId());

        applyRequest(address, request);
        // isDefault=false diabaikan: alamat utama cuma bisa "dilepas" dengan memilih alamat lain
        if (Boolean.TRUE.equals(request.isDefault())) {
            makeDefault(user.getId(), address);
        }
        return AddressResponse.from(address);
    }

    @Transactional
    public AddressResponse setDefault(String email, UUID id) {
        User user = getUser(email);
        UserAddress address = getOwnedAddress(id, user.getId());
        makeDefault(user.getId(), address);
        return AddressResponse.from(address);
    }

    @Transactional
    public void delete(String email, UUID id) {
        User user = getUser(email);
        UserAddress address = getOwnedAddress(id, user.getId());
        boolean wasDefault = address.getIsDefault();

        addressRepository.delete(address);
        addressRepository.flush();

        // Kalau yang dihapus alamat utama, alamat tertua yang tersisa naik jadi utama
        if (wasDefault) {
            addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtAsc(user.getId()).stream()
                    .findFirst()
                    .ifPresent(a -> a.setIsDefault(true));
        }
    }

    // Satu user cuma boleh punya satu alamat utama
    private void makeDefault(UUID userId, UserAddress target) {
        addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtAsc(userId)
                .forEach(a -> a.setIsDefault(a.getId().equals(target.getId())));
        target.setIsDefault(true);
    }

    private void applyRequest(UserAddress address, AddressRequest request) {
        address.setLabel(request.label());
        address.setRecipientName(request.recipientName());
        address.setPhone(request.phone());
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setProvince(request.province());
        address.setPostalCode(request.postalCode());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    // Alamat milik user lain dianggap tidak ada (404), bukan 403,
    // supaya orang nggak bisa nebak id alamat mana yang ada.
    private UserAddress getOwnedAddress(UUID id, UUID userId) {
        return addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Alamat tidak ditemukan: " + id));
    }
}
