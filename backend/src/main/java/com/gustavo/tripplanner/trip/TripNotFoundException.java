package com.gustavo.tripplanner.trip;

public class TripNotFoundException extends RuntimeException {

    public TripNotFoundException() {
        super("Viagem não encontrada");
    }
}
