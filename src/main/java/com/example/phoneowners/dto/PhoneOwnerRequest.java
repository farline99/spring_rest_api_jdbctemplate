package com.example.phoneowners.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PhoneOwnerRequest(
        @NotBlank(message = "Фамилия обязательна")
        @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
        String lastName,

        @NotBlank(message = "Имя обязательно")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String firstName,

        @NotBlank(message = "Отчество обязательно")
        @Size(max = 100, message = "Отчество не должно превышать 100 символов")
        String middleName,

        @NotNull(message = "Адрес обязателен")
        @Valid
        AddressRequest address,

        @NotBlank(message = "Номер телефона обязателен")
        @Pattern(regexp = "^[+0-9()\\-\\s]{7,30}$", message = "Некорректный номер телефона")
        String phoneNumber
) {
}
