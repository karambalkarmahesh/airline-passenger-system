package com.airline.flightservice.repository;

import com.airline.flightservice.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    boolean existsByFlightNumber(String flightNumber);

    Optional<Flight> findByFlightNumber(String flightNumber);

    List<Flight> findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(
            String origin,
            String destination,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}