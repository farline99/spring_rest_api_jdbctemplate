package com.example.phoneowners.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Почтовый индекс обязателен")
        @Size(max = 20, message = "Почтовый индекс не должен превышать 20 символов")
        String postalCode,

        @NotBlank(message = "Страна обязательна")
        @Size(max = 100, message = "Название страны не должно превышать 100 символов")
        String country,

        @NotBlank(message = "Область обязательна")
        @Size(max = 100, message = "Название области не должно превышать 100 символов")
        String region,

        @NotBlank(message = "Район обязателен")
        @Size(max = 100, message = "Название района не должно превышать 100 символов")
        String district,

        @NotBlank(message = "Город обязателен")
        @Size(max = 100, message = "Название города не должно превышать 100 символов")
        String city,

        @NotBlank(message = "Улица обязательна")
        @Size(max = 150, message = "Название улицы не должно превышать 150 символов")
        String street,

        @NotBlank(message = "Номер дома обязателен")
        @Size(max = 30, message = "Номер дома не должен превышать 30 символов")
        String house,

        @NotBlank(message = "Номер квартиры обязателен")
        @Size(max = 30, message = "Номер квартиры не должен превышать 30 символов")
        String apartment
) {
}
