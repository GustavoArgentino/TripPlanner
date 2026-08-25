package com.gustavo.tripplanner.itinerary;

public class InvalidItineraryDateException extends RuntimeException {

    public InvalidItineraryDateException() {
        super("A data do item deve estar dentro do período da viagem");
    }
}
