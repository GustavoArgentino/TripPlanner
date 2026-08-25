package com.gustavo.tripplanner.trip;

import com.gustavo.tripplanner.trip.dto.TripRequest;
import com.gustavo.tripplanner.trip.dto.TripResponse;
import com.gustavo.tripplanner.user.User;
import com.gustavo.tripplanner.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    private TripService tripService;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        tripService = new TripService(tripRepository, userRepository);

        owner = new User("owner@example.com", "hash", "Owner");
        ReflectionTestUtils.setField(owner, "id", UUID.randomUUID());

        otherUser = new User("other@example.com", "hash", "Other");
        ReflectionTestUtils.setField(otherUser, "id", UUID.randomUUID());
    }

    private TripRequest validRequest() {
        return new TripRequest(
                "Trip to Rio",
                "Rio de Janeiro",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 10),
                "Vacation"
        );
    }

    @Test
    void createSavesTripOwnedByAuthenticatedUser() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TripResponse response = tripService.create("owner@example.com", validRequest());

        assertThat(response.name()).isEqualTo("Trip to Rio");
        ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
        verify(tripRepository).save(captor.capture());
        assertThat(captor.getValue().getOwner()).isEqualTo(owner);
    }

    @Test
    void createRejectsEndDateBeforeStartDate() {
        TripRequest request = new TripRequest(
                "Bad Trip", "Nowhere", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 1), null
        );

        assertThatThrownBy(() -> tripService.create("owner@example.com", request))
                .isInstanceOf(InvalidTripDatesException.class);
    }

    @Test
    void listReturnsOnlyOwnersTrips() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        Trip trip = new Trip(owner, "Trip", "Dest", LocalDate.now(), LocalDate.now().plusDays(1), null);
        when(tripRepository.findAllByOwnerIdOrderByStartDateAsc(owner.getId())).thenReturn(List.of(trip));

        List<TripResponse> result = tripService.listOwn("owner@example.com");

        assertThat(result).hasSize(1);
        verify(tripRepository).findAllByOwnerIdOrderByStartDateAsc(owner.getId());
    }

    @Test
    void getReturnsOwnTrip() {
        UUID tripId = UUID.randomUUID();
        Trip trip = new Trip(owner, "Trip", "Dest", LocalDate.now(), LocalDate.now().plusDays(1), null);
        ReflectionTestUtils.setField(trip, "id", tripId);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.findByIdAndOwnerId(tripId, owner.getId())).thenReturn(Optional.of(trip));

        TripResponse response = tripService.getOwn("owner@example.com", tripId);

        assertThat(response.id()).isEqualTo(tripId);
    }

    @Test
    void getRejectsAnotherUsersTripWithNotFound() {
        UUID tripId = UUID.randomUUID();
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(tripRepository.findByIdAndOwnerId(tripId, otherUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.getOwn("other@example.com", tripId))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void updateModifiesOwnTrip() {
        UUID tripId = UUID.randomUUID();
        Trip trip = new Trip(owner, "Old Name", "Old Dest", LocalDate.now(), LocalDate.now().plusDays(1), null);
        ReflectionTestUtils.setField(trip, "id", tripId);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.findByIdAndOwnerId(tripId, owner.getId())).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TripResponse response = tripService.update("owner@example.com", tripId, validRequest());

        assertThat(response.name()).isEqualTo("Trip to Rio");
    }

    @Test
    void updateRejectsAnotherUsersTripWithNotFound() {
        UUID tripId = UUID.randomUUID();
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(tripRepository.findByIdAndOwnerId(tripId, otherUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.update("other@example.com", tripId, validRequest()))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void deleteRemovesOwnTrip() {
        UUID tripId = UUID.randomUUID();
        Trip trip = new Trip(owner, "Trip", "Dest", LocalDate.now(), LocalDate.now().plusDays(1), null);
        ReflectionTestUtils.setField(trip, "id", tripId);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.findByIdAndOwnerId(tripId, owner.getId())).thenReturn(Optional.of(trip));

        tripService.delete("owner@example.com", tripId);

        verify(tripRepository).delete(trip);
    }

    @Test
    void deleteRejectsAnotherUsersTripWithNotFound() {
        UUID tripId = UUID.randomUUID();
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(tripRepository.findByIdAndOwnerId(tripId, otherUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.delete("other@example.com", tripId))
                .isInstanceOf(TripNotFoundException.class);
    }
}
