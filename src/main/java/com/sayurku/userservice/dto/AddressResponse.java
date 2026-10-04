package com.sayurku.userservice.dto;

import com.sayurku.userservice.entity.UserAddress;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String label,
        String recipientName,
        String phone,
        String street,
        String city,
        String province,
        String postalCode,
        boolean isDefault
) {
    public static AddressResponse from(UserAddress a) {
        return new AddressResponse(
                a.getId(),
                a.getLabel(),
                a.getRecipientName(),
                a.getPhone(),
                a.getStreet(),
                a.getCity(),
                a.getProvince(),
                a.getPostalCode(),
                a.getIsDefault()
        );
    }
}
