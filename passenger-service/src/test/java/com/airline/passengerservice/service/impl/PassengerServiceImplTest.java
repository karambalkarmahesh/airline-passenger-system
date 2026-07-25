package com.airline.passengerservice.service.impl;

import com.airline.passengerservice.dto.PassengerRequestDTO;
import com.airline.passengerservice.dto.PassengerResponseDTO;
import com.airline.passengerservice.entity.Passenger;
import com.airline.passengerservice.exception.DuplicateResourceException;
import com.airline.passengerservice.exception.ResourceNotFoundException;
import com.airline.passengerservice.mapper.PassengerMapper;
import com.airline.passengerservice.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {

    private static final Long PASSENGER_ID = 1L;
    private static final String EMAIL = "mahesh@example.com";
    private static final String PASSPORT_NUMBER = "P1234567";

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @Mock
    private PassengerRequestDTO request;

    @Mock
    private Passenger passenger;

    @Mock
    private Passenger savedPassenger;

    @Mock
    private PassengerResponseDTO response;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    @BeforeEach
    void setUp() {
        /*
         * Mockito automatically creates the mocks and injects them into
         * PassengerServiceImpl because of @ExtendWith and @InjectMocks.
         */
    }

    // =========================================================
    // CREATE PASSENGER TESTS
    // =========================================================

    @Nested
    @DisplayName("Create Passenger Tests")
    class CreatePassengerTests {

        @Test
        @DisplayName("Should create passenger successfully")
        void shouldCreatePassengerSuccessfully() {

            when(request.getEmail()).thenReturn(EMAIL);
            when(request.getPassportNumber()).thenReturn(PASSPORT_NUMBER);

            when(passengerRepository.existsByPassportNumber(PASSPORT_NUMBER))
                    .thenReturn(false);

            when(passengerRepository.existsByEmail(EMAIL))
                    .thenReturn(false);

            when(passengerMapper.toEntity(request))
                    .thenReturn(passenger);

            when(passengerRepository.save(passenger))
                    .thenReturn(savedPassenger);

            when(savedPassenger.getId())
                    .thenReturn(PASSENGER_ID);

            when(passengerMapper.toResponseDTO(savedPassenger))
                    .thenReturn(response);

            PassengerResponseDTO result =
                    passengerService.createPassenger(request);

            assertNotNull(result);
            assertSame(response, result);

            verify(passengerRepository)
                    .existsByPassportNumber(PASSPORT_NUMBER);

            verify(passengerRepository)
                    .existsByEmail(EMAIL);

            verify(passengerMapper)
                    .toEntity(request);

            verify(passenger)
                    .setCreatedAt(any());

            verify(passenger)
                    .setUpdatedAt(any());

            verify(passengerRepository)
                    .save(passenger);

            verify(passengerMapper)
                    .toResponseDTO(savedPassenger);
        }

        @Test
        @DisplayName("Should throw exception when passport already exists")
        void shouldThrowDuplicateResourceExceptionWhenPassportAlreadyExists() {

            when(request.getEmail()).thenReturn(EMAIL);
            when(request.getPassportNumber()).thenReturn(PASSPORT_NUMBER);

            when(passengerRepository.existsByPassportNumber(PASSPORT_NUMBER))
                    .thenReturn(true);

            DuplicateResourceException exception = assertThrows(
                    DuplicateResourceException.class,
                    () -> passengerService.createPassenger(request)
            );

            assertEquals(
                    "Passenger already exists with passport number: "
                            + PASSPORT_NUMBER,
                    exception.getMessage()
            );

            verify(passengerRepository)
                    .existsByPassportNumber(PASSPORT_NUMBER);

            verify(passengerRepository, never())
                    .existsByEmail(anyString());

            verify(passengerRepository, never())
                    .save(any(Passenger.class));

            verify(passengerMapper, never())
                    .toEntity(any(PassengerRequestDTO.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowDuplicateResourceExceptionWhenEmailAlreadyExists() {

            when(request.getEmail()).thenReturn(EMAIL);
            when(request.getPassportNumber()).thenReturn(PASSPORT_NUMBER);

            when(passengerRepository.existsByPassportNumber(PASSPORT_NUMBER))
                    .thenReturn(false);

            when(passengerRepository.existsByEmail(EMAIL))
                    .thenReturn(true);

            DuplicateResourceException exception = assertThrows(
                    DuplicateResourceException.class,
                    () -> passengerService.createPassenger(request)
            );

            assertEquals(
                    "Passenger already exists with email: " + EMAIL,
                    exception.getMessage()
            );

            verify(passengerRepository)
                    .existsByPassportNumber(PASSPORT_NUMBER);

            verify(passengerRepository)
                    .existsByEmail(EMAIL);

            verify(passengerRepository, never())
                    .save(any(Passenger.class));

            verify(passengerMapper, never())
                    .toEntity(any(PassengerRequestDTO.class));
        }
    }

    // =========================================================
    // GET PASSENGER BY ID TESTS
    // =========================================================

    @Nested
    @DisplayName("Get Passenger By ID Tests")
    class GetPassengerByIdTests {

        @Test
        @DisplayName("Should return passenger when ID exists")
        void shouldReturnPassengerWhenIdExists() {

            when(passengerRepository.findById(PASSENGER_ID))
                    .thenReturn(Optional.of(passenger));

            when(passengerMapper.toResponseDTO(passenger))
                    .thenReturn(response);

            PassengerResponseDTO result =
                    passengerService.getPassengerById(PASSENGER_ID);

            assertNotNull(result);
            assertSame(response, result);

            verify(passengerRepository)
                    .findById(PASSENGER_ID);

            verify(passengerMapper)
                    .toResponseDTO(passenger);
        }

        @Test
        @DisplayName("Should throw exception when passenger ID does not exist")
        void shouldThrowResourceNotFoundExceptionWhenPassengerDoesNotExist() {

            when(passengerRepository.findById(PASSENGER_ID))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> passengerService.getPassengerById(PASSENGER_ID)
            );

            assertEquals(
                    "Passenger not found with ID: " + PASSENGER_ID,
                    exception.getMessage()
            );

            verify(passengerRepository)
                    .findById(PASSENGER_ID);

            verify(passengerMapper, never())
                    .toResponseDTO(any(Passenger.class));
        }
    }

    // =========================================================
    // GET ALL PASSENGERS TESTS
    // =========================================================

    @Nested
    @DisplayName("Get All Passengers Tests")
    class GetAllPassengersTests {

        @Test
        @DisplayName("Should return all passengers")
        void shouldReturnAllPassengers() {

            Passenger passengerTwo = mock(Passenger.class);

            PassengerResponseDTO responseTwo =
                    mock(PassengerResponseDTO.class);

            List<Passenger> passengers =
                    List.of(passenger, passengerTwo);

            List<PassengerResponseDTO> expectedResponses =
                    List.of(response, responseTwo);

            when(passengerRepository.findAll())
                    .thenReturn(passengers);

            when(passengerMapper.toResponseDTO(passengers))
                    .thenReturn(expectedResponses);

            List<PassengerResponseDTO> result =
                    passengerService.getAllPassengers();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertSame(expectedResponses, result);

            verify(passengerRepository).findAll();

            verify(passengerMapper)
                    .toResponseDTO(passengers);
        }

        @Test
        @DisplayName("Should return empty list when no passengers exist")
        void shouldReturnEmptyListWhenNoPassengersExist() {

            List<Passenger> emptyPassengerList =
                    Collections.emptyList();

            List<PassengerResponseDTO> emptyResponseList =
                    Collections.emptyList();

            when(passengerRepository.findAll())
                    .thenReturn(emptyPassengerList);

            when(passengerMapper.toResponseDTO(emptyPassengerList))
                    .thenReturn(emptyResponseList);

            List<PassengerResponseDTO> result =
                    passengerService.getAllPassengers();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(passengerRepository).findAll();

            verify(passengerMapper)
                    .toResponseDTO(emptyPassengerList);
        }
    }

    // =========================================================
    // UPDATE PASSENGER TESTS
    // =========================================================

    @Nested
    @DisplayName("Update Passenger Tests")
    class UpdatePassengerTests {

        @Test
        @DisplayName("Should update passenger successfully")
        void shouldUpdatePassengerSuccessfully() {

            when(request.getEmail()).thenReturn(EMAIL);
            when(request.getPassportNumber()).thenReturn(PASSPORT_NUMBER);

            when(passengerRepository.findById(PASSENGER_ID))
                    .thenReturn(Optional.of(passenger));

            when(passengerRepository.existsByEmail(EMAIL))
                    .thenReturn(false);

            when(passengerRepository.existsByPassportNumber(PASSPORT_NUMBER))
                    .thenReturn(false);

            when(passengerRepository.save(passenger))
                    .thenReturn(savedPassenger);

            when(passengerMapper.toResponseDTO(savedPassenger))
                    .thenReturn(response);

            PassengerResponseDTO result =
                    passengerService.updatePassengerById(
                            PASSENGER_ID,
                            request
                    );

            assertNotNull(result);
            assertSame(response, result);

            verify(passengerRepository)
                    .findById(PASSENGER_ID);

            verify(passengerRepository)
                    .existsByEmail(EMAIL);

            verify(passengerRepository)
                    .existsByPassportNumber(PASSPORT_NUMBER);

            verify(passengerMapper)
                    .updatePassengerFromRequest(request, passenger);

            verify(passenger)
                    .setUpdatedAt(any());

            verify(passengerRepository)
                    .save(passenger);

            verify(passengerMapper)
                    .toResponseDTO(savedPassenger);
        }

        @Test
        @DisplayName("Should throw exception when updating missing passenger")
        void shouldThrowResourceNotFoundExceptionWhenUpdatingMissingPassenger() {

            when(passengerRepository.findById(PASSENGER_ID))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> passengerService.updatePassengerById(
                            PASSENGER_ID,
                            request
                    )
            );

            assertEquals(
                    "Passenger not found with id: " + PASSENGER_ID,
                    exception.getMessage()
            );

            verify(passengerRepository)
                    .findById(PASSENGER_ID);

            verify(passengerRepository, never())
                    .save(any(Passenger.class));

            verify(passengerMapper, never())
                    .updatePassengerFromRequest(
                            any(PassengerRequestDTO.class),
                            any(Passenger.class)
                    );
        }

        @Test
        @DisplayName("Should throw exception when update email already exists")
        void shouldThrowDuplicateResourceExceptionWhenUpdateEmailExists() {

            when(request.getEmail()).thenReturn(EMAIL);

            when(passengerRepository.findById(PASSENGER_ID))
                    .thenReturn(Optional.of(passenger));

            when(passengerRepository.existsByEmail(EMAIL))
                    .thenReturn(true);

            DuplicateResourceException exception = assertThrows(
                    DuplicateResourceException.class,
                    () -> passengerService.updatePassengerById(
                            PASSENGER_ID,
                            request
                    )
            );

            assertEquals(
                    "Passenger already exists with email: " + EMAIL,
                    exception.getMessage()
            );

            verify(passengerRepository)
                    .findById(PASSENGER_ID);

            verify(passengerRepository)
                    .existsByEmail(EMAIL);

            verify(passengerRepository, never())
                    .existsByPassportNumber(anyString());

            verify(passengerRepository, never())
                    .save(any(Passenger.class));

            verify(passengerMapper, never())
                    .updatePassengerFromRequest(
                            any(PassengerRequestDTO.class),
                            any(Passenger.class)
                    );
        }

        @Test
        @DisplayName("Should throw exception when update passport already exists")
        void shouldThrowDuplicateResourceExceptionWhenUpdatePassportExists() {

            when(request.getEmail()).thenReturn(EMAIL);
            when(request.getPassportNumber()).thenReturn(PASSPORT_NUMBER);

            when(passengerRepository.findById(PASSENGER_ID))
                    .thenReturn(Optional.of(passenger));

            when(passengerRepository.existsByEmail(EMAIL))
                    .thenReturn(false);

            when(passengerRepository.existsByPassportNumber(PASSPORT_NUMBER))
                    .thenReturn(true);

            DuplicateResourceException exception = assertThrows(
                    DuplicateResourceException.class,
                    () -> passengerService.updatePassengerById(
                            PASSENGER_ID,
                            request
                    )
            );

            assertEquals(
                    "Passenger already exists with passport number: "
                            + PASSPORT_NUMBER,
                    exception.getMessage()
            );

            verify(passengerRepository)
                    .findById(PASSENGER_ID);

            verify(passengerRepository)
                    .existsByEmail(EMAIL);

            verify(passengerRepository)
                    .existsByPassportNumber(PASSPORT_NUMBER);

            verify(passengerRepository, never())
                    .save(any(Passenger.class));

            verify(passengerMapper, never())
                    .updatePassengerFromRequest(
                            any(PassengerRequestDTO.class),
                            any(Passenger.class)
                    );
        }
    }

    // =========================================================
    // DELETE PASSENGER TESTS
    // =========================================================

    @Nested
    @DisplayName("Delete Passenger Tests")
    class DeletePassengerTests {

        @Test
        @DisplayName("Should delete passenger successfully")
        void shouldDeletePassengerSuccessfully() {

            when(passengerRepository.findById(PASSENGER_ID))
                    .thenReturn(Optional.of(passenger));

            assertDoesNotThrow(
                    () -> passengerService.deletePassenger(PASSENGER_ID)
            );

            verify(passengerRepository)
                    .findById(PASSENGER_ID);

            verify(passengerRepository)
                    .delete(passenger);
        }

        @Test
        @DisplayName("Should throw exception when deleting missing passenger")
        void shouldThrowResourceNotFoundExceptionWhenDeletingMissingPassenger() {

            when(passengerRepository.findById(PASSENGER_ID))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> passengerService.deletePassenger(PASSENGER_ID)
            );

            assertEquals(
                    "Passenger not found with id: " + PASSENGER_ID,
                    exception.getMessage()
            );

            verify(passengerRepository)
                    .findById(PASSENGER_ID);

            verify(passengerRepository, never())
                    .delete(any(Passenger.class));
        }
    }
}