package com.gustavo.tripplanner.itinerary;

import com.gustavo.tripplanner.itinerary.dto.ItineraryItemRequest;
import com.gustavo.tripplanner.itinerary.dto.ItineraryItemResponse;
import com.gustavo.tripplanner.trip.Trip;
import com.gustavo.tripplanner.trip.TripNotFoundException;
import com.gustavo.tripplanner.trip.TripRepository;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItineraryItemServiceTest {

    @Mock
    private ItineraryItemRepository itineraryItemRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    private ItineraryItemService itineraryItemService;

    private User owner;
    private User otherUser;
    private Trip trip;
    private UUID tripId;

    @BeforeEach
    void setUp() {
        itineraryItemService = new ItineraryItemService(itineraryItemRepository, tripRepository, userRepository);

        owner = new User("owner@example.com", "hash", "Owner");
        ReflectionTestUtils.setField(owner, "id", UUID.randomUUID());

        otherUser = new User("other@example.com", "hash", "Other");
        ReflectionTestUtils.setField(otherUser, "id", UUID.randomUUID());

        trip = new Trip(owner, "Trip to Rio", "Rio de Janeiro", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 10), null);
        tripId = UUID.randomUUID();
        ReflectionTestUtils.setField(trip, "id", tripId);
    }

    private ItineraryItemRequest validRequest() {
        return new ItineraryItemRequest("Visitar o Pão de Açúcar", LocalDate.of(2026, 9, 3), LocalTime.of(9, 0), "Urca", "Levar protetor solar");
    }

    @Test
    void createSavesItemOnOwnTrip() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.findByIdAndOwnerId(tripId, owner.getId())).thenReturn(Optional.of(trip));
        when(itineraryItemRepository.save(any(ItineraryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItineraryItemResponse response = itineraryItemService.create("owner@example.com", tripId, validRequest());

        assertThat(response.title()).isEqualTo("Visitar o Pão de Açúcar");
        ArgumentCaptor<ItineraryItem> captor = ArgumentCaptor.forClass(ItineraryItem.class);
        verify(itineraryItemRepository).save(captor.capture());
        assertThat(captor.getValue().getTrip()).isEqualTo(trip);
    }

    @Test
    void createRejectsAnotherUsersTripWithNotFound() {
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(tripRepository.findByIdAndOwnerId(tripId, otherUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itineraryItemService.create("other@example.com", tripId, validRequest()))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void createRejectsDateOutsideTripRange() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.findByIdAndOwnerId(tripId, owner.getId())).thenReturn(Optional.of(trip));

        ItineraryItemRequest request = new ItineraryItemRequest("Fora do período", LocalDate.of(2026, 9, 20), null, null, null);

        assertThatThrownBy(() -> itineraryItemService.create("owner@example.com", tripId, request))
                .isInstanceOf(InvalidItineraryDateException.class);
    }

    @Test
    void listReturnsOnlyTripsOwnItems() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.findByIdAndOwnerId(tripId, owner.getId())).thenReturn(Optional.of(trip));
        ItineraryItem item = new ItineraryItem(trip, "Item", LocalDate.of(2026, 9, 3), null, null, null);
        when(itineraryItemRepository.findAllByTripIdOrderByDateAscStartTimeAsc(tripId)).thenReturn(List.of(item));

        List<ItineraryItemResponse> result = itineraryItemService.list("owner@example.com", tripId);

        assertThat(result).hasSize(1);
    }

    @Test
    void getReturnsOwnItem() {
        UUID itemId = UUID.randomUUID();
        ItineraryItem item = new ItineraryItem(trip, "Item", LocalDate.of(2026, 9, 3), null, null, null);
        ReflectionTestUtils.setField(item, "id", itemId);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.findByIdAndOwnerId(tripId, owner.getId())).thenReturn(Optional.of(trip));
        when(itineraryItemRepository.findByIdAndTripId(itemId, tripId)).thenReturn(Optional.of(item));

        ItineraryItemResponse response = itineraryItemService.get("owner@example.com", tripId, itemId);

        assertThat(response.id()).isEqualTo(itemId);
    }

    @Test
    void getRejectsItemFromAnotherUsersTripWithNotFound() {
        UUID itemId = UUID.randomUUID();
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(tripRepository.findByIdAndOwnerId(tripId, otherUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itineraryItemService.get("other@example.com", tripId, itemId))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void updateModifiesOwnItem() {
        UUID itemId = UUID.randomUUID();
        ItineraryItem item = new ItineraryItem(trip, "Old title", LocalDate.of(2026, 9, 3), null, null, null);
        ReflectionTestUtils.setField(item, "id", itemId);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.findByIdAndOwnerId(tripId, owner.getId())).thenReturn(Optional.of(trip));
        when(itineraryItemRepository.findByIdAndTripId(itemId, tripId)).thenReturn(Optional.of(item));
        when(itineraryItemRepository.save(any(ItineraryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItineraryItemResponse response = itineraryItemService.update("owner@example.com", tripId, itemId, validRequest());

        assertThat(response.title()).isEqualTo("Visitar o Pão de Açúcar");
    }

    @Test
    void updateRejectsItemFromAnotherUsersTripWithNotFound() {
        UUID itemId = UUID.randomUUID();
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(tripRepository.findByIdAndOwnerId(tripId, otherUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itineraryItemService.update("other@example.com", tripId, itemId, validRequest()))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void deleteRemovesOwnItem() {
        UUID itemId = UUID.randomUUID();
        ItineraryItem item = new ItineraryItem(trip, "Item", LocalDate.of(2026, 9, 3), null, null, null);
        ReflectionTestUtils.setField(item, "id", itemId);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(tripRepository.findByIdAndOwnerId(tripId, owner.getId())).thenReturn(Optional.of(trip));
        when(itineraryItemRepository.findByIdAndTripId(itemId, tripId)).thenReturn(Optional.of(item));

        itineraryItemService.delete("owner@example.com", tripId, itemId);

        verify(itineraryItemRepository).delete(item);
    }

    @Test
    void deleteRejectsItemFromAnotherUsersTripWithNotFound() {
        UUID itemId = UUID.randomUUID();
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(tripRepository.findByIdAndOwnerId(tripId, otherUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itineraryItemService.delete("other@example.com", tripId, itemId))
                .isInstanceOf(TripNotFoundException.class);
    }
}
