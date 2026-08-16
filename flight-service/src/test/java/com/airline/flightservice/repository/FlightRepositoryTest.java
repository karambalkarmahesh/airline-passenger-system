package com.airline.flightservice.repository;

import com.airline.flightservice.entity.Flight;
import com.airline.flightservice.enums.FlightStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FlightRepositoryTest {

    @Autowired
    private FlightRepository flightRepository;

    private Flight flight;

    @BeforeEach
    void setUp() {

        flightRepository.deleteAll();

        flight = Flight.builder()
                .flightNumber("AI101")
                .airlineCode("AI")
                .origin("BOM")
                .destination("DEL")
                .departureTime(
                        LocalDateTime.of(
                                2026, 9, 15, 10, 0
                        )
                )
                .arrivalTime(
                        LocalDateTime.of(
                                2026, 9, 15, 12, 0
                        )
                )
                .aircraftType("A320")
                .availableSeats(150)
                .status(FlightStatus.SCHEDULED)
                .build();
    }

    @Test
    void shouldSaveFlight() {

        Flight saved =
                flightRepository.save(flight);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void shouldFindFlightById() {

        Flight saved =
                flightRepository.save(flight);

        Optional<Flight> result =
                flightRepository.findById(saved.getId());

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindFlightByFlightNumber() {

        flightRepository.save(flight);

        Optional<Flight> result =
                flightRepository.findByFlightNumber("AI101");

        assertThat(result).isPresent();
        assertThat(result.get().getOrigin())
                .isEqualTo("BOM");
    }

    @Test
    void shouldReturnTrueWhenFlightNumberExists() {

        flightRepository.save(flight);

        assertThat(
                flightRepository.existsByFlightNumber("AI101")
        ).isTrue();
    }

    @Test
    void shouldReturnFalseWhenFlightNumberDoesNotExist() {

        assertThat(
                flightRepository.existsByFlightNumber("XX999")
        ).isFalse();
    }

    @Test
    void shouldSearchFlightsByRouteAndDate() {

        flightRepository.save(flight);

        LocalDate date =
                LocalDate.of(2026, 9, 15);

        List<Flight> result =
                flightRepository
                        .findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(
                                "BOM",
                                "DEL",
                                date.atStartOfDay(),
                                date.plusDays(1)
                                        .atStartOfDay()
                                        .minusNanos(1)
                        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFlightNumber())
                .isEqualTo("AI101");
    }

    @Test
    void shouldSearchRouteIgnoringCase() {

        flightRepository.save(flight);

        LocalDate date =
                LocalDate.of(2026, 9, 15);

        List<Flight> result =
                flightRepository
                        .findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(
                                "bom",
                                "del",
                                date.atStartOfDay(),
                                date.plusDays(1)
                                        .atStartOfDay()
                                        .minusNanos(1)
                        );

        assertThat(result).hasSize(1);
    }
}