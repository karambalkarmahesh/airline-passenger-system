package com.airline.flightservice.dto;

import com.airline.flightservice.enums.FlightStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightRequestDTO {

    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    @NotBlank(message = "Airline code is required")
    private String airlineCode;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Departure time is required")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalTime;

    private String aircraftType;

    @NotNull(message = "Available seats is required")
    @PositiveOrZero(message = "Available seats cannot be negative")
    private Integer availableSeats;

    @NotNull(message = "Flight status is required")
    private FlightStatus status;
}