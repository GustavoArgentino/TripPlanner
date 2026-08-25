package com.gustavo.tripplanner.itinerary;

import com.gustavo.tripplanner.itinerary.dto.ItineraryItemRequest;
import com.gustavo.tripplanner.itinerary.dto.ItineraryItemResponse;
import com.gustavo.tripplanner.trip.Trip;
import com.gustavo.tripplanner.trip.TripNotFoundException;
import com.gustavo.tripplanner.trip.TripRepository;
import com.gustavo.tripplanner.user.User;
import com.gustavo.tripplanner.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ItineraryItemService {

    private final ItineraryItemRepository itineraryItemRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public ItineraryItemService(
            ItineraryItemRepository itineraryItemRepository,
            TripRepository tripRepository,
            UserRepository userRepository
    ) {
        this.itineraryItemRepository = itineraryItemRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public ItineraryItemResponse create(String ownerEmail, UUID tripId, ItineraryItemRequest request) {
        Trip trip = findOwnedTrip(ownerEmail, tripId);
        validateDateWithinTrip(trip, request.date());

        ItineraryItem item = new ItineraryItem(
                trip, request.title(), request.date(), request.startTime(), request.location(), request.notes()
        );
        return ItineraryItemResponse.from(itineraryItemRepository.save(item));
    }

    public List<ItineraryItemResponse> list(String ownerEmail, UUID tripId) {
        Trip trip = findOwnedTrip(ownerEmail, tripId);
        return itineraryItemRepository.findAllByTripIdOrderByDateAscStartTimeAsc(trip.getId())
                .stream()
                .map(ItineraryItemResponse::from)
                .toList();
    }

    public ItineraryItemResponse get(String ownerEmail, UUID tripId, UUID itemId) {
        return ItineraryItemResponse.from(findOwnedItem(ownerEmail, tripId, itemId));
    }

    public ItineraryItemResponse update(String ownerEmail, UUID tripId, UUID itemId, ItineraryItemRequest request) {
        ItineraryItem item = findOwnedItem(ownerEmail, tripId, itemId);
        validateDateWithinTrip(item.getTrip(), request.date());

        item.update(request.title(), request.date(), request.startTime(), request.location(), request.notes());
        return ItineraryItemResponse.from(itineraryItemRepository.save(item));
    }

    public void delete(String ownerEmail, UUID tripId, UUID itemId) {
        ItineraryItem item = findOwnedItem(ownerEmail, tripId, itemId);
        itineraryItemRepository.delete(item);
    }

    private ItineraryItem findOwnedItem(String ownerEmail, UUID tripId, UUID itemId) {
        Trip trip = findOwnedTrip(ownerEmail, tripId);
        return itineraryItemRepository.findByIdAndTripId(itemId, trip.getId())
                .orElseThrow(ItineraryItemNotFoundException::new);
    }

    private Trip findOwnedTrip(String ownerEmail, UUID tripId) {
        User owner = userRepository.findByEmail(ownerEmail).orElseThrow();
        return tripRepository.findByIdAndOwnerId(tripId, owner.getId())
                .orElseThrow(TripNotFoundException::new);
    }

    private void validateDateWithinTrip(Trip trip, LocalDate date) {
        if (date.isBefore(trip.getStartDate()) || date.isAfter(trip.getEndDate())) {
            throw new InvalidItineraryDateException();
        }
    }
}
