package com.gustavo.tripplanner.trip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    List<Trip> findAllByOwnerIdOrderByStartDateAsc(UUID ownerId);

    Optional<Trip> findByIdAndOwnerId(UUID id, UUID ownerId);
}
