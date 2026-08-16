package com.airline.flightservice.service;

import com.airline.flightservice.dto.FlightRequestDTO;
import com.airline.flightservice.dto.FlightResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {

    FlightResponseDTO createFlight(FlightRequestDTO request);

    FlightResponseDTO getFlightById(Long id);

    List<FlightResponseDTO> getAllFlights();

    FlightResponseDTO updateFlight(Long id, FlightRequestDTO request);

    void deleteFlight(Long id);

    List<FlightResponseDTO> searchFlights(
            String origin,
            String destination,
            LocalDate date
    );
}