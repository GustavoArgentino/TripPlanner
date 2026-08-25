package com.gustavo.tripplanner.trip;

import com.gustavo.tripplanner.trip.dto.TripRequest;
import com.gustavo.tripplanner.trip.dto.TripResponse;
import com.gustavo.tripplanner.user.User;
import com.gustavo.tripplanner.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public TripService(TripRepository tripRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public TripResponse create(String ownerEmail, TripRequest request) {
        validateDates(request);
        User owner = resolveOwner(ownerEmail);
        Trip trip = new Trip(owner, request.name(), request.destination(), request.startDate(), request.endDate(), request.description());
        return TripResponse.from(tripRepository.save(trip));
    }

    public List<TripResponse> listOwn(String ownerEmail) {
        User owner = resolveOwner(ownerEmail);
        return tripRepository.findAllByOwnerIdOrderByStartDateAsc(owner.getId())
                .stream()
                .map(TripResponse::from)
                .toList();
    }

    public TripResponse getOwn(String ownerEmail, UUID tripId) {
        return TripResponse.from(findOwnedTrip(ownerEmail, tripId));
    }

    public TripResponse update(String ownerEmail, UUID tripId, TripRequest request) {
        validateDates(request);
        Trip trip = findOwnedTrip(ownerEmail, tripId);
        trip.update(request.name(), request.destination(), request.startDate(), request.endDate(), request.description());
        return TripResponse.from(tripRepository.save(trip));
    }

    public void delete(String ownerEmail, UUID tripId) {
        Trip trip = findOwnedTrip(ownerEmail, tripId);
        tripRepository.delete(trip);
    }

    private Trip findOwnedTrip(String ownerEmail, UUID tripId) {
        User owner = resolveOwner(ownerEmail);
        return tripRepository.findByIdAndOwnerId(tripId, owner.getId())
                .orElseThrow(TripNotFoundException::new);
    }

    private User resolveOwner(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    private void validateDates(TripRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new InvalidTripDatesException();
        }
    }
}
