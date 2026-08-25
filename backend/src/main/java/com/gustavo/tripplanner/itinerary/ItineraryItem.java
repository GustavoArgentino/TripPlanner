package com.gustavo.tripplanner.itinerary;

import com.gustavo.tripplanner.trip.Trip;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "itinerary_items")
public class ItineraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false, updatable = false)
    // Deleting a trip must not fail on the FK constraint once it has
    // itinerary items — cascade the delete at the DB level rather than
    // requiring TripService to know about this module.
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Trip trip;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    @Column
    private LocalTime startTime;

    @Column
    private String location;

    @Column
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected ItineraryItem() {
    }

    public ItineraryItem(Trip trip, String title, LocalDate date, LocalTime startTime, String location, String notes) {
        this.trip = trip;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.location = location;
        this.notes = notes;
    }

    public void update(String title, LocalDate date, LocalTime startTime, String location, String notes) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.location = location;
        this.notes = notes;
    }

    public UUID getId() {
        return id;
    }

    public Trip getTrip() {
        return trip;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
