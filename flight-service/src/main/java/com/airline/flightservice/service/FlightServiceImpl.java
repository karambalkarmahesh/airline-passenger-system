package com.airline.flightservice.service;

import com.airline.flightservice.dto.FlightRequestDTO;
import com.airline.flightservice.dto.FlightResponseDTO;
import com.airline.flightservice.entity.Flight;
import com.airline.flightservice.exceptions.DuplicateResourceException;
import com.airline.flightservice.exceptions.InvalidFlightScheduleException;
import com.airline.flightservice.exceptions.ResourceNotFoundException;
import com.airline.flightservice.mapper.FlightMapper;
import com.airline.flightservice.repository.FlightRepository;
import com.airline.flightservice.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    @Override
    @Transactional
    public FlightResponseDTO createFlight(FlightRequestDTO request) {

        validateFlightSchedule(request);

        if (flightRepository.existsByFlightNumber(
                request.getFlightNumber()
        )) {

            throw new DuplicateResourceException(
                    "Flight already exists with flight number: "
                            + request.getFlightNumber()
            );
        }

        Flight flight = flightMapper.toEntity(request);

        Flight savedFlight =
                flightRepository.save(flight);

        return flightMapper.toResponseDTO(savedFlight);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponseDTO getFlightById(Long id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flight not found with ID: " + id
                        )
                );

        return flightMapper.toResponseDTO(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponseDTO> getAllFlights() {

        List<Flight> flights = flightRepository.findAll();

        return flights.stream()
                .map(flightMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public FlightResponseDTO updateFlight(
            Long id,
            FlightRequestDTO request
    ) {

        validateFlightSchedule(request);

        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flight not found with ID: " + id
                        )
                );

        if (!existingFlight.getFlightNumber()
                .equals(request.getFlightNumber())
                && flightRepository.existsByFlightNumber(
                        request.getFlightNumber()
                )) {

            throw new DuplicateResourceException(
                    "Flight already exists with flight number: "
                            + request.getFlightNumber()
            );
        }

        flightMapper.updateFlightFromRequest(
                request,
                existingFlight
        );

        Flight updatedFlight =
                flightRepository.save(existingFlight);

        return flightMapper.toResponseDTO(updatedFlight);
    }

    @Override
    @Transactional
    public void deleteFlight(Long id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flight not found with ID: " + id
                        )
                );

        flightRepository.delete(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponseDTO> searchFlights(
            String origin,
            String destination,
            LocalDate date
    ) {

        LocalDateTime startTime = date.atStartOfDay();

        LocalDateTime endTime = date
                .plusDays(1)
                .atStartOfDay()
                .minusNanos(1);

        List<Flight> flights =
                flightRepository
                        .findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(
                                origin,
                                destination,
                                startTime,
                                endTime
                        );

        return flights.stream()
                .map(flightMapper::toResponseDTO)
                .toList();
    }

    private void validateFlightSchedule(FlightRequestDTO request) {

        if (!request.getArrivalTime()
                .isAfter(request.getDepartureTime())) {

            throw new InvalidFlightScheduleException(
                    "Arrival time must be after departure time"
            );
        }
    }
}