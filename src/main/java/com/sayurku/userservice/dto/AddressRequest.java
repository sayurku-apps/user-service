package com.sayurku.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(

        @NotBlank(message = "Label wajib diisi (misal: Rumah, Kantor)")
        @Size(max = 50, message = "Label maksimal 50 karakter")
        String label,

        @NotBlank(message = "Nama penerima wajib diisi")
        @Size(max = 100, message = "Nama penerima maksimal 100 karakter")
        String recipientName,

        @NotBlank(message = "Nomor HP wajib diisi")
        @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Nomor HP tidak valid")
        String phone,

        @NotBlank(message = "Alamat lengkap wajib diisi")
        String street,

        @NotBlank(message = "Kota wajib diisi")
        @Size(max = 100, message = "Kota maksimal 100 karakter")
        String city,

        @NotBlank(message = "Provinsi wajib diisi")
        @Size(max = 100, message = "Provinsi maksimal 100 karakter")
        String province,

        @NotBlank(message = "Kode pos wajib diisi")
        @Pattern(regexp = "^[0-9]{5}$", message = "Kode pos harus 5 digit")
        String postalCode,

        // Opsional. true = jadikan alamat utama
        Boolean isDefault
) {}
