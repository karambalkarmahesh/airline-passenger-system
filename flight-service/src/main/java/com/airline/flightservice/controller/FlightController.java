package com.airline.flightservice.controller;

import com.airline.flightservice.dto.FlightRequestDTO;
import com.airline.flightservice.dto.FlightResponseDTO;
import com.airline.flightservice.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<FlightResponseDTO> createFlight(
            @Valid @RequestBody FlightRequestDTO request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(flightService.createFlight(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> getFlightById(
            @PathVariable("id") Long id
    ) {

        return ResponseEntity.ok(
                flightService.getFlightById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<FlightResponseDTO>> getAllFlights() {

        return ResponseEntity.ok(
                flightService.getAllFlights()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> updateFlight(
            @PathVariable("id") Long id,
            @Valid @RequestBody FlightRequestDTO request
    ) {

        return ResponseEntity.ok(
                flightService.updateFlight(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(
            @PathVariable("id") Long id
    ) {

        flightService.deleteFlight(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightResponseDTO>> searchFlights(

            @RequestParam("origin") String origin,

            @RequestParam("destination") String destination,

            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {

        return ResponseEntity.ok(
                flightService.searchFlights(
                        origin,
                        destination,
                        date
                )
        );
    }
}