package com.airline.flightservice.dto;

import com.airline.flightservice.enums.FlightStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightResponseDTO {

    private Long id;
    private String flightNumber;
    private String airlineCode;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String aircraftType;
    private Integer availableSeats;
    private FlightStatus status;
}