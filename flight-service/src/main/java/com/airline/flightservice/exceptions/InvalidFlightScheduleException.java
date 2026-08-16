package com.airline.flightservice.exceptions;

public class InvalidFlightScheduleException extends RuntimeException {

    public InvalidFlightScheduleException(String message) {
        super(message);
    }
}