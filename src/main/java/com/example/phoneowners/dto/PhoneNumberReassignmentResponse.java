package com.example.phoneowners.dto;

public record PhoneNumberReassignmentResponse(
        PhoneOwnerResponse sourceOwner,
        PhoneOwnerResponse targetOwner
) {
}
