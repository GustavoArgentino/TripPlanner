package com.gustavo.tripplanner.itinerary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItineraryItemRepository extends JpaRepository<ItineraryItem, UUID> {

    List<ItineraryItem> findAllByTripIdOrderByDateAscStartTimeAsc(UUID tripId);

    Optional<ItineraryItem> findByIdAndTripId(UUID id, UUID tripId);
}
