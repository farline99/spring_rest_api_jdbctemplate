package com.example.phoneowners.dto;

import jakarta.validation.constraints.Size;

public record AddressPatchRequest(
        @Size(max = 20, message = "Почтовый индекс не должен превышать 20 символов")
        String postalCode,

        @Size(max = 100, message = "Название страны не должно превышать 100 символов")
        String country,

        @Size(max = 100, message = "Название области не должно превышать 100 символов")
        String region,

        @Size(max = 100, message = "Название района не должно превышать 100 символов")
        String district,

        @Size(max = 100, message = "Название города не должно превышать 100 символов")
        String city,

        @Size(max = 150, message = "Название улицы не должно превышать 150 символов")
        String street,

        @Size(max = 30, message = "Номер дома не должен превышать 30 символов")
        String house,

        @Size(max = 30, message = "Номер квартиры не должен превышать 30 символов")
        String apartment
) {
}
