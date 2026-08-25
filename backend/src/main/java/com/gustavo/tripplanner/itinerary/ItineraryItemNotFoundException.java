package com.gustavo.tripplanner.itinerary;

public class ItineraryItemNotFoundException extends RuntimeException {

    public ItineraryItemNotFoundException() {
        super("Item de itinerário não encontrado");
    }
}
