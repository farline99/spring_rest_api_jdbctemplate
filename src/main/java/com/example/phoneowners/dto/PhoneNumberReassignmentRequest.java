package com.example.phoneowners.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PhoneNumberReassignmentRequest(
        @NotNull(message = "Идентификатор второго владельца обязателен")
        Long targetOwnerId,

        @NotBlank(message = "Номер телефона обязателен")
        @Pattern(regexp = "^[+0-9()\\-\\s]{7,30}$", message = "Некорректный номер телефона")
        String phoneNumber
) {
}
