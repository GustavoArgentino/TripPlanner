package com.gustavo.tripplanner.itinerary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ItineraryItemRequest(

        @NotBlank
        String title,

        @NotNull
        LocalDate date,

        LocalTime startTime,

        String location,

        String notes
) {
}
