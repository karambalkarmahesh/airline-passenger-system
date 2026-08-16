package com.airline.flightservice.controller;

import com.airline.flightservice.dto.FlightRequestDTO;
import com.airline.flightservice.dto.FlightResponseDTO;
import com.airline.flightservice.enums.FlightStatus;
import com.airline.flightservice.exceptions.DuplicateResourceException;
import com.airline.flightservice.exceptions.GlobalExceptionHandler;
import com.airline.flightservice.exceptions.ResourceNotFoundException;
import com.airline.flightservice.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    @Mock
    private FlightService flightService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new FlightController(flightService)
                )
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }

    private String validJson() {
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

    private FlightResponseDTO response() {

        return FlightResponseDTO.builder()
                .id(1L)
                .flightNumber("AI101")
                .airlineCode("AI")
                .origin("BOM")
                .destination("DEL")
                .departureTime(
                        LocalDateTime.of(2026, 9, 15, 10, 0)
                )
                .arrivalTime(
                        LocalDateTime.of(2026, 9, 15, 12, 0)
                )
                .availableSeats(150)
                .status(FlightStatus.SCHEDULED)
                .build();
    }

    @Test
    void shouldCreateFlight() throws Exception {

        when(flightService.createFlight(
                any(FlightRequestDTO.class)
        )).thenReturn(response());

        mockMvc.perform(
                        post("/api/v1/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJson())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.flightNumber")
                        .value("AI101"));
    }

    @Test
    void shouldReturn400ForInvalidFlight() throws Exception {

        String json = """
                {
                  "flightNumber": "",
                  "airlineCode": "",
                  "origin": "",
                  "destination": "",
                  "availableSeats": -1
                }
                """;

        mockMvc.perform(
                        post("/api/v1/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(flightService);
    }

    @Test
    void shouldReturn409ForDuplicateFlight() throws Exception {

        when(flightService.createFlight(any()))
                .thenThrow(
                        new DuplicateResourceException(
                                "Flight already exists"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJson())
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetFlightById() throws Exception {

        when(flightService.getFlightById(1L))
                .thenReturn(response());

        mockMvc.perform(
                        get("/api/v1/flights/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber")
                        .value("AI101"));
    }

    @Test
    void shouldReturn404WhenFlightNotFound() throws Exception {

        when(flightService.getFlightById(99L))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Flight not found with ID: 99"
                        )
                );

        mockMvc.perform(
                        get("/api/v1/flights/{id}", 99L)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllFlights() throws Exception {

        when(flightService.getAllFlights())
                .thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/v1/flights")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightNumber")
                        .value("AI101"));
    }

    @Test
    void shouldUpdateFlight() throws Exception {

        when(flightService.updateFlight(
                eq(1L),
                any(FlightRequestDTO.class)
        )).thenReturn(response());

        mockMvc.perform(
                        put("/api/v1/flights/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJson())
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteFlight() throws Exception {

        doNothing()
                .when(flightService)
                .deleteFlight(1L);

        mockMvc.perform(
                        delete("/api/v1/flights/{id}", 1L)
                )
                .andExpect(status().isNoContent());

        verify(flightService)
                .deleteFlight(1L);
    }

    @Test
    void shouldSearchFlights() throws Exception {

        when(flightService.searchFlights(
                eq("BOM"),
                eq("DEL"),
                eq(LocalDate.of(2026, 9, 15))
        )).thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/v1/flights/search")
                                .param("origin", "BOM")
                                .param("destination", "DEL")
                                .param("date", "2026-09-15")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightNumber")
                        .value("AI101"));
    }

    @Test
    void shouldReturnEmptyListWhenSearchHasNoMatches()
            throws Exception {

        when(flightService.searchFlights(
                anyString(),
                anyString(),
                any(LocalDate.class)
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/v1/flights/search")
                                .param("origin", "BOM")
                                .param("destination", "JFK")
                                .param("date", "2026-09-15")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}