package com.example.phoneowners.dto;

public record AddressResponse(
        Long id,
        String postalCode,
        String country,
        String region,
        String district,
        String city,
        String street,
        String house,
        String apartment
) {
}
