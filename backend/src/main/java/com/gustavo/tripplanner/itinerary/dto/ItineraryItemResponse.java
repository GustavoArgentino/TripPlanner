package com.gustavo.tripplanner.itinerary.dto;

import com.gustavo.tripplanner.itinerary.ItineraryItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ItineraryItemResponse(
        UUID id,
        UUID tripId,
        String title,
        LocalDate date,
        LocalTime startTime,
        String location,
        String notes
) {

    public static ItineraryItemResponse from(ItineraryItem item) {
        return new ItineraryItemResponse(
                item.getId(),
                item.getTrip().getId(),
                item.getTitle(),
                item.getDate(),
                item.getStartTime(),
                item.getLocation(),
                item.getNotes()
        );
    }
}
