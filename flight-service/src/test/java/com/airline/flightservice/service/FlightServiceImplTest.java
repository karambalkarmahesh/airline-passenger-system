package com.airline.flightservice.service;

import com.airline.flightservice.dto.FlightRequestDTO;
import com.airline.flightservice.dto.FlightResponseDTO;
import com.airline.flightservice.entity.Flight;
import com.airline.flightservice.enums.FlightStatus;
import com.airline.flightservice.exceptions.DuplicateResourceException;
import com.airline.flightservice.exceptions.InvalidFlightScheduleException;
import com.airline.flightservice.exceptions.ResourceNotFoundException;
import com.airline.flightservice.mapper.FlightMapper;
import com.airline.flightservice.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightServiceImpl flightService;

    private FlightRequestDTO request;
    private Flight flight;
    private FlightResponseDTO response;

    @BeforeEach
    void setUp() {

        request = FlightRequestDTO.builder()
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
                .aircraftType("A320")
                .availableSeats(150)
                .status(FlightStatus.SCHEDULED)
                .build();

        flight = Flight.builder()
                .id(1L)
                .flightNumber("AI101")
                .airlineCode("AI")
                .origin("BOM")
                .destination("DEL")
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .aircraftType("A320")
                .availableSeats(150)
                .status(FlightStatus.SCHEDULED)
                .build();

        response = FlightResponseDTO.builder()
                .id(1L)
                .flightNumber("AI101")
                .airlineCode("AI")
                .origin("BOM")
                .destination("DEL")
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .aircraftType("A320")
                .availableSeats(150)
                .status(FlightStatus.SCHEDULED)
                .build();
    }

    @Test
    void shouldCreateFlightSuccessfully() {

        when(flightRepository.existsByFlightNumber("AI101"))
                .thenReturn(false);

        when(flightMapper.toEntity(request))
                .thenReturn(flight);

        when(flightRepository.save(flight))
                .thenReturn(flight);

        when(flightMapper.toResponseDTO(flight))
                .thenReturn(response);

        FlightResponseDTO result =
                flightService.createFlight(request);

        assertNotNull(result);
        assertEquals("AI101", result.getFlightNumber());
        assertEquals("BOM", result.getOrigin());

        verify(flightRepository).save(flight);
    }

    @Test
    void shouldThrowDuplicateExceptionWhenFlightNumberExists() {

        when(flightRepository.existsByFlightNumber("AI101"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> flightService.createFlight(request)
        );

        verify(flightRepository, never()).save(any());
    }

    @Test
    void shouldRejectInvalidScheduleDuringCreate() {

        request.setArrivalTime(
                request.getDepartureTime().minusHours(1)
        );

        assertThrows(
                InvalidFlightScheduleException.class,
                () -> flightService.createFlight(request)
        );

        verifyNoInteractions(flightRepository);
    }

    @Test
    void shouldGetFlightById() {

        when(flightRepository.findById(1L))
                .thenReturn(Optional.of(flight));

        when(flightMapper.toResponseDTO(flight))
                .thenReturn(response);

        FlightResponseDTO result =
                flightService.getFlightById(1L);

        assertEquals(1L, result.getId());
        assertEquals("AI101", result.getFlightNumber());
    }

    @Test
    void shouldThrowNotFoundWhenFlightIdDoesNotExist() {

        when(flightRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> flightService.getFlightById(99L)
        );
    }

    @Test
    void shouldReturnAllFlights() {

        Flight secondFlight = Flight.builder()
                .id(2L)
                .flightNumber("6E501")
                .build();

        FlightResponseDTO secondResponse =
                FlightResponseDTO.builder()
                        .id(2L)
                        .flightNumber("6E501")
                        .build();

        when(flightRepository.findAll())
                .thenReturn(List.of(flight, secondFlight));

        when(flightMapper.toResponseDTO(flight))
                .thenReturn(response);

        when(flightMapper.toResponseDTO(secondFlight))
                .thenReturn(secondResponse);

        List<FlightResponseDTO> result =
                flightService.getAllFlights();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoFlightsExist() {

        when(flightRepository.findAll())
                .thenReturn(List.of());

        assertTrue(
                flightService.getAllFlights().isEmpty()
        );
    }

    @Test
    void shouldUpdateFlightSuccessfully() {

        when(flightRepository.findById(1L))
                .thenReturn(Optional.of(flight));

        when(flightRepository.save(flight))
                .thenReturn(flight);

        when(flightMapper.toResponseDTO(flight))
                .thenReturn(response);

        FlightResponseDTO result =
                flightService.updateFlight(1L, request);

        assertNotNull(result);

        verify(flightMapper)
                .updateFlightFromRequest(request, flight);

        verify(flightRepository).save(flight);
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingMissingFlight() {

        when(flightRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> flightService.updateFlight(99L, request)
        );
    }

    @Test
    void shouldRejectDuplicateFlightNumberDuringUpdate() {

        request.setFlightNumber("AI999");

        when(flightRepository.findById(1L))
                .thenReturn(Optional.of(flight));

        when(flightRepository.existsByFlightNumber("AI999"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> flightService.updateFlight(1L, request)
        );
    }

    @Test
    void shouldDeleteFlightSuccessfully() {

        when(flightRepository.findById(1L))
                .thenReturn(Optional.of(flight));

        flightService.deleteFlight(1L);

        verify(flightRepository).delete(flight);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingMissingFlight() {

        when(flightRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> flightService.deleteFlight(99L)
        );
    }

    @Test
    void shouldSearchFlightsSuccessfully() {

        LocalDate date =
                LocalDate.of(2026, 9, 15);

        when(
                flightRepository
                        .findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(
                                eq("BOM"),
                                eq("DEL"),
                                any(LocalDateTime.class),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(List.of(flight));

        when(flightMapper.toResponseDTO(flight))
                .thenReturn(response);

        List<FlightResponseDTO> result =
                flightService.searchFlights(
                        "BOM",
                        "DEL",
                        date
                );

        assertEquals(1, result.size());
        assertEquals("AI101", result.get(0).getFlightNumber());
    }
}