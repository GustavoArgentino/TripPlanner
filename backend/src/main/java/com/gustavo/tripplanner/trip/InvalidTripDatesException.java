package com.gustavo.tripplanner.trip;

public class InvalidTripDatesException extends RuntimeException {

    public InvalidTripDatesException() {
        super("A data de término não pode ser anterior à data de início");
    }
}
