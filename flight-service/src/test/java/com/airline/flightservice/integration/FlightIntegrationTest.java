package com.airline.flightservice.integration;

import com.airline.flightservice.entity.Flight;
import com.airline.flightservice.enums.FlightStatus;
import com.airline.flightservice.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FlightIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepository;

    @BeforeEach
    void setUp() {
        flightRepository.deleteAll();
    }

    private Flight createFlight(
            String flightNumber,
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime
    ) {

        Flight flight = Flight.builder()
                .flightNumber(flightNumber)
                .airlineCode("AI")
                .origin(origin)
                .destination(destination)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .aircraftType("A320")
                .availableSeats(150)
                .status(FlightStatus.SCHEDULED)
                .build();

        return flightRepository.save(flight);
    }

    private String validFlightJson() {

        return """
                {
                  "flightNumber": "AI101",
                  "airlineCode": "AI",
                  "origin": "BOM",
                  "destination": "DEL",
                  "departureTime": "2026-09-15T10:00:00",
                  "arrivalTime": "2026-09-15T12:00:00",
                  "aircraftType": "A320",
                  "availableSeats": 150,
                  "status": "SCHEDULED"
                }
                """;
    }

    @Test
    @DisplayName("Should create flight successfully")
    void shouldCreateFlightSuccessfully() throws Exception {

        mockMvc.perform(
                        post("/api/v1/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validFlightJson())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value("AI101"))
                .andExpect(jsonPath("$.origin").value("BOM"))
                .andExpect(jsonPath("$.destination").value("DEL"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        assertThat(
                flightRepository.existsByFlightNumber("AI101")
        ).isTrue();
    }

    @Test
    @DisplayName("Should return 409 for duplicate flight number")
    void shouldReturnConflictForDuplicateFlight() throws Exception {

        createFlight(
                "AI101",
                "BOM",
                "DEL",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        mockMvc.perform(
                        post("/api/v1/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validFlightJson())
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Should return 400 for invalid flight request")
    void shouldReturnBadRequestForInvalidFlight() throws Exception {

        String invalidRequest = """
                {
                  "flightNumber": "",
                  "airlineCode": "",
                  "origin": "",
                  "destination": "",
                  "availableSeats": -5
                }
                """;

        mockMvc.perform(
                        post("/api/v1/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when arrival is before departure")
    void shouldRejectInvalidFlightSchedule() throws Exception {

        String request = """
                {
                  "flightNumber": "AI999",
                  "airlineCode": "AI",
                  "origin": "BOM",
                  "destination": "DEL",
                  "departureTime": "2026-09-15T15:00:00",
                  "arrivalTime": "2026-09-15T12:00:00",
                  "aircraftType": "A320",
                  "availableSeats": 150,
                  "status": "SCHEDULED"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Arrival time must be after departure time"));
    }

    @Test
    @DisplayName("Should get flight by ID")
    void shouldGetFlightById() throws Exception {

        Flight flight = createFlight(
                "AI101",
                "BOM",
                "DEL",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        mockMvc.perform(
                        get("/api/v1/flights/{id}", flight.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AI101"));
    }

    @Test
    @DisplayName("Should return 404 when flight does not exist")
    void shouldReturnNotFoundForMissingFlight() throws Exception {

        mockMvc.perform(
                        get("/api/v1/flights/{id}", 999L)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should return all flights")
    void shouldReturnAllFlights() throws Exception {

        createFlight(
                "AI101",
                "BOM",
                "DEL",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        createFlight(
                "6E501",
                "BOM",
                "BLR",
                LocalDateTime.of(2026, 9, 15, 14, 0),
                LocalDateTime.of(2026, 9, 15, 16, 0)
        );

        mockMvc.perform(
                        get("/api/v1/flights")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightNumber").exists())
                .andExpect(jsonPath("$[1].flightNumber").exists());
    }

    @Test
    @DisplayName("Should update flight successfully")
    void shouldUpdateFlightSuccessfully() throws Exception {

        Flight flight = createFlight(
                "AI101",
                "BOM",
                "DEL",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        String updateRequest = """
                {
                  "flightNumber": "AI101",
                  "airlineCode": "AI",
                  "origin": "BOM",
                  "destination": "DEL",
                  "departureTime": "2026-09-15T10:00:00",
                  "arrivalTime": "2026-09-15T12:30:00",
                  "aircraftType": "A321",
                  "availableSeats": 120,
                  "status": "BOARDING"
                }
                """;

        mockMvc.perform(
                        put("/api/v1/flights/{id}", flight.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(120))
                .andExpect(jsonPath("$.status").value("BOARDING"))
                .andExpect(jsonPath("$.aircraftType").value("A321"));

        Flight updated = flightRepository
                .findById(flight.getId())
                .orElseThrow();

        assertThat(updated.getAvailableSeats()).isEqualTo(120);
        assertThat(updated.getStatus()).isEqualTo(FlightStatus.BOARDING);
    }

    @Test
    @DisplayName("Should return 400 for invalid schedule during update")
    void shouldRejectInvalidScheduleDuringUpdate() throws Exception {

        Flight flight = createFlight(
                "AI101",
                "BOM",
                "DEL",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        String request = """
                {
                  "flightNumber": "AI101",
                  "airlineCode": "AI",
                  "origin": "BOM",
                  "destination": "DEL",
                  "departureTime": "2026-09-15T15:00:00",
                  "arrivalTime": "2026-09-15T12:00:00",
                  "aircraftType": "A320",
                  "availableSeats": 150,
                  "status": "SCHEDULED"
                }
                """;

        mockMvc.perform(
                        put("/api/v1/flights/{id}", flight.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should search flights by origin destination and date")
    void shouldSearchFlightsSuccessfully() throws Exception {

        createFlight(
                "AI101",
                "BOM",
                "DEL",
                LocalDateTime.of(2026, 9, 15, 6, 30),
                LocalDateTime.of(2026, 9, 15, 8, 45)
        );

        createFlight(
                "6E501",
                "BOM",
                "DEL",
                LocalDateTime.of(2026, 9, 15, 18, 30),
                LocalDateTime.of(2026, 9, 15, 20, 40)
        );

        createFlight(
                "AI119",
                "BOM",
                "JFK",
                LocalDateTime.of(2026, 9, 16, 1, 30),
                LocalDateTime.of(2026, 9, 16, 15, 30)
        );

        mockMvc.perform(
                        get("/api/v1/flights/search")
                                .param("origin", "bom")
                                .param("destination", "del")
                                .param("date", "2026-09-15")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should return empty list when search has no matches")
    void shouldReturnEmptySearchResult() throws Exception {

        mockMvc.perform(
                        get("/api/v1/flights/search")
                                .param("origin", "BOM")
                                .param("destination", "JFK")
                                .param("date", "2026-09-15")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Should delete flight successfully")
    void shouldDeleteFlightSuccessfully() throws Exception {

        Flight flight = createFlight(
                "AI101",
                "BOM",
                "DEL",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        mockMvc.perform(
                        delete("/api/v1/flights/{id}", flight.getId())
                )
                .andExpect(status().isNoContent());

        assertThat(
                flightRepository.findById(flight.getId())
        ).isEmpty();
    }
}