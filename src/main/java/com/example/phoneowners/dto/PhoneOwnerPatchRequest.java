package com.example.phoneowners.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PhoneOwnerPatchRequest(
        @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
        String lastName,

        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String firstName,

        @Size(max = 100, message = "Отчество не должно превышать 100 символов")
        String middleName,

        @Valid
        AddressPatchRequest address,

        @Pattern(regexp = "^[+0-9()\\-\\s]{7,30}$", message = "Некорректный номер телефона")
        String phoneNumber
) {
}
