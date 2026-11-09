package com.example.phoneowners.dto;

import java.util.Map;

public record PhoneOwnerStatistics(
        long totalOwners,
        long distinctCountries,
        long distinctCities,
        String mostCommonCity,
        String mostCommonStreet,
        Map<String, Long> ownersByCountry,
        Map<String, Long> ownersByCity
) {
}
