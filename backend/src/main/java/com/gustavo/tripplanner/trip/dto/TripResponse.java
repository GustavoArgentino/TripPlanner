package com.gustavo.tripplanner.trip.dto;

import com.gustavo.tripplanner.trip.Trip;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TripResponse(
        UUID id,
        String name,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        Instant createdAt
) {

    public static TripResponse from(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getName(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getDescription(),
                trip.getCreatedAt()
        );
    }
}
