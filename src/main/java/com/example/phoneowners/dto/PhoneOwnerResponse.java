package com.example.phoneowners.dto;

public record PhoneOwnerResponse(
        Long id,
        String lastName,
        String firstName,
        String middleName,
        AddressResponse address,
        String phoneNumber
) {
}
