package com.gustavo.tripplanner.trip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TripRequest(

        @NotBlank
        String name,

        @NotBlank
        String destination,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        String description
) {
}
