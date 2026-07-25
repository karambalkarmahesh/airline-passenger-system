package com.airline.passengerservice.controller;

import com.airline.passengerservice.dto.PassengerRequestDTO;
import com.airline.passengerservice.dto.PassengerResponseDTO;
import com.airline.passengerservice.enums.Gender;
import com.airline.passengerservice.exception.DuplicateResourceException;
import com.airline.passengerservice.exception.GlobalExceptionHandler;
import com.airline.passengerservice.exception.ResourceNotFoundException;
import com.airline.passengerservice.service.PassengerService;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PassengerControllerTest {

    private static final Long PASSENGER_ID = 1L;
    private static final String FIRST_NAME = "Mahesh";
    private static final String LAST_NAME = "Karambalkar";
    private static final String EMAIL = "mahesh@example.com";
    private static final String PHONE_NUMBER = "9876543210";
    private static final String PASSPORT_NUMBER = "P1234567";
    private static final String NATIONALITY = "Indian";
    private static final LocalDate DATE_OF_BIRTH =
            LocalDate.of(1998, 1, 14);

    @Mock
    private PassengerService passengerService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        PassengerController passengerController =
                new PassengerController(passengerService);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(passengerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(
                        new MappingJackson2HttpMessageConverter(objectMapper)
                )
                .build();
    }
    // =========================================================
    // CREATE PASSENGER
    // =========================================================

    @Nested
    @DisplayName("Create Passenger API Tests")
    class CreatePassengerTests {

        @Test
        @DisplayName("Should create passenger and return 201")
        void shouldCreatePassengerSuccessfully() throws Exception {

            PassengerResponseDTO response = createResponseDTO();

            when(passengerService.createPassenger(
                    any(PassengerRequestDTO.class)
            )).thenReturn(response);

            mockMvc.perform(
                            post("/api/passengers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(validPassengerJson())
                    )
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$.id").value(PASSENGER_ID))
                    .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                    .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.phoneNumber").value(PHONE_NUMBER))
                    .andExpect(jsonPath("$.passportNumber")
                            .value(PASSPORT_NUMBER))
                    .andExpect(jsonPath("$.nationality")
                            .value(NATIONALITY))
                    .andExpect(jsonPath("$.dateOfBirth")
                            .value("1998-01-14"))
                    .andExpect(jsonPath("$.gender").value("MALE"));

            verify(passengerService).createPassenger(
                    argThat(request ->
                            FIRST_NAME.equals(request.getFirstName())
                                    && LAST_NAME.equals(request.getLastName())
                                    && EMAIL.equals(request.getEmail())
                                    && PHONE_NUMBER.equals(
                                    request.getPhoneNumber()
                            )
                                    && PASSPORT_NUMBER.equals(
                                    request.getPassportNumber()
                            )
                                    && NATIONALITY.equals(
                                    request.getNationality()
                            )
                                    && DATE_OF_BIRTH.equals(
                                    request.getDateOfBirth()
                            )
                                    && Gender.MALE == request.getGender()
                    )
            );
        }

        @Test
        @DisplayName("Should return 400 when create request is invalid")
        void shouldReturnBadRequestWhenCreateRequestIsInvalid()
                throws Exception {

            String invalidRequest = """
                    {
                      "firstName": "",
                      "lastName": "",
                      "email": "invalid-email",
                      "phoneNumber": "123",
                      "passportNumber": "",
                      "nationality": "",
                      "dateOfBirth": null,
                      "gender": null
                    }
                    """;

            mockMvc.perform(
                            post("/api/passengers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest)
                    )
                    .andExpect(status().isBadRequest());

            verify(passengerService, never())
                    .createPassenger(any(PassengerRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 409 when passenger already exists")
        void shouldReturnConflictWhenCreatingDuplicatePassenger()
                throws Exception {

            String message =
                    "Passenger already exists with email: " + EMAIL;

            when(passengerService.createPassenger(
                    any(PassengerRequestDTO.class)
            )).thenThrow(new DuplicateResourceException(message));

            mockMvc.perform(
                            post("/api/passengers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(validPassengerJson())
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value(message));

            verify(passengerService)
                    .createPassenger(any(PassengerRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 400 when request body is missing")
        void shouldReturnBadRequestWhenCreateBodyIsMissing()
                throws Exception {

            mockMvc.perform(
                            post("/api/passengers")
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }

        @Test
        @DisplayName("Should return 400 when JSON is malformed")
        void shouldReturnBadRequestWhenCreateJsonIsMalformed()
                throws Exception {

            String malformedJson = """
                    {
                      "firstName": "Mahesh",
                      "email":
                    }
                    """;

            mockMvc.perform(
                            post("/api/passengers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(malformedJson)
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }

        @Test
        @DisplayName("Should return 400 when phone number is invalid")
        void shouldReturnBadRequestWhenPhoneNumberIsInvalid()
                throws Exception {

            String invalidPhoneRequest = """
                    {
                      "firstName": "Mahesh",
                      "lastName": "Karambalkar",
                      "email": "mahesh@example.com",
                      "phoneNumber": "12345",
                      "passportNumber": "P1234567",
                      "nationality": "Indian",
                      "dateOfBirth": "1998-01-14",
                      "gender": "MALE"
                    }
                    """;

            mockMvc.perform(
                            post("/api/passengers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidPhoneRequest)
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }

        @Test
        @DisplayName("Should return 400 when date of birth is in future")
        void shouldReturnBadRequestWhenDateOfBirthIsInFuture()
                throws Exception {

            String futureDateRequest = """
                    {
                      "firstName": "Mahesh",
                      "lastName": "Karambalkar",
                      "email": "mahesh@example.com",
                      "phoneNumber": "9876543210",
                      "passportNumber": "P1234567",
                      "nationality": "Indian",
                      "dateOfBirth": "2099-01-14",
                      "gender": "MALE"
                    }
                    """;

            mockMvc.perform(
                            post("/api/passengers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(futureDateRequest)
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }

        @Test
        @DisplayName("Should return 400 when gender value is invalid")
        void shouldReturnBadRequestWhenGenderIsInvalid()
                throws Exception {

            String invalidGenderRequest = """
                    {
                      "firstName": "Mahesh",
                      "lastName": "Karambalkar",
                      "email": "mahesh@example.com",
                      "phoneNumber": "9876543210",
                      "passportNumber": "P1234567",
                      "nationality": "Indian",
                      "dateOfBirth": "1998-01-14",
                      "gender": "INVALID"
                    }
                    """;

            mockMvc.perform(
                            post("/api/passengers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidGenderRequest)
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }
    }

    // =========================================================
    // GET PASSENGER BY ID
    // =========================================================

    @Nested
    @DisplayName("Get Passenger By ID API Tests")
    class GetPassengerByIdTests {

        @Test
        @DisplayName("Should return passenger and 200")
        void shouldReturnPassengerWhenIdExists() throws Exception {

            when(passengerService.getPassengerById(PASSENGER_ID))
                    .thenReturn(createResponseDTO());

            mockMvc.perform(
                            get("/api/passengers/{id}", PASSENGER_ID)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$.id").value(PASSENGER_ID))
                    .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                    .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.phoneNumber").value(PHONE_NUMBER))
                    .andExpect(jsonPath("$.passportNumber")
                            .value(PASSPORT_NUMBER))
                    .andExpect(jsonPath("$.nationality")
                            .value(NATIONALITY))
                    .andExpect(jsonPath("$.dateOfBirth")
                            .value("1998-01-14"))
                    .andExpect(jsonPath("$.gender").value("MALE"));

            verify(passengerService).getPassengerById(PASSENGER_ID);
        }

        @Test
        @DisplayName("Should return 404 when passenger does not exist")
        void shouldReturnNotFoundWhenPassengerDoesNotExist()
                throws Exception {

            String message =
                    "Passenger not found with ID: " + PASSENGER_ID;

            when(passengerService.getPassengerById(PASSENGER_ID))
                    .thenThrow(new ResourceNotFoundException(message));

            mockMvc.perform(
                            get("/api/passengers/{id}", PASSENGER_ID)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(message));

            verify(passengerService).getPassengerById(PASSENGER_ID);
        }

        @Test
        @DisplayName("Should return 400 when passenger ID is invalid")
        void shouldReturnBadRequestWhenPassengerIdIsInvalid()
                throws Exception {

            mockMvc.perform(
                            get("/api/passengers/{id}", "abc")
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }
    }

    // =========================================================
    // GET ALL PASSENGERS
    // =========================================================

    @Nested
    @DisplayName("Get All Passengers API Tests")
    class GetAllPassengersTests {

        @Test
        @DisplayName("Should return all passengers and 200")
        void shouldReturnAllPassengers() throws Exception {

            PassengerResponseDTO firstPassenger =
                    createResponseDTO();

            PassengerResponseDTO secondPassenger =
                    createSecondResponseDTO();

            when(passengerService.getAllPassengers())
                    .thenReturn(List.of(
                            firstPassenger,
                            secondPassenger
                    ));

            mockMvc.perform(
                            get("/api/passengers")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].email").value(EMAIL))
                    .andExpect(jsonPath("$[0].phoneNumber")
                            .value(PHONE_NUMBER))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].email")
                            .value("sonal@example.com"))
                    .andExpect(jsonPath("$[1].phoneNumber")
                            .value("9876543211"));

            verify(passengerService).getAllPassengers();
        }

        @Test
        @DisplayName("Should return empty array when no passengers exist")
        void shouldReturnEmptyListWhenNoPassengersExist()
                throws Exception {

            when(passengerService.getAllPassengers())
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(
                            get("/api/passengers")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(passengerService).getAllPassengers();
        }
    }

    // =========================================================
    // UPDATE PASSENGER
    // =========================================================

    @Nested
    @DisplayName("Update Passenger API Tests")
    class UpdatePassengerTests {

        @Test
        @DisplayName("Should update passenger and return 200")
        void shouldUpdatePassengerSuccessfully() throws Exception {

            PassengerResponseDTO response = createResponseDTO();

            String requestJson =
                    objectMapper.writeValueAsString(createRequestDTO());

            when(passengerService.updatePassengerById(
                    eq(PASSENGER_ID),
                    any(PassengerRequestDTO.class)
            )).thenReturn(response);

            mockMvc.perform(
                            put("/api/passengers/{id}", PASSENGER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(requestJson)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$.id").value(PASSENGER_ID))
                    .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                    .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.phoneNumber").value(PHONE_NUMBER))
                    .andExpect(jsonPath("$.passportNumber")
                            .value(PASSPORT_NUMBER))
                    .andExpect(jsonPath("$.nationality").value(NATIONALITY))
                    .andExpect(jsonPath("$.dateOfBirth")
                            .value(DATE_OF_BIRTH.toString()))
                    .andExpect(jsonPath("$.gender")
                            .value(Gender.MALE.name()));

            verify(passengerService, times(1))
                    .updatePassengerById(
                            eq(PASSENGER_ID),
                            argThat(request ->
                                    FIRST_NAME.equals(request.getFirstName())
                                            && LAST_NAME.equals(request.getLastName())
                                            && EMAIL.equals(request.getEmail())
                                            && PHONE_NUMBER.equals(
                                            request.getPhoneNumber()
                                    )
                                            && PASSPORT_NUMBER.equals(
                                            request.getPassportNumber()
                                    )
                                            && NATIONALITY.equals(
                                            request.getNationality()
                                    )
                                            && DATE_OF_BIRTH.equals(
                                            request.getDateOfBirth()
                                    )
                                            && Gender.MALE == request.getGender()
                            )
                    );

            verifyNoMoreInteractions(passengerService);
        }
        @Test
        @DisplayName("Should return 400 when update request is invalid")
        void shouldReturnBadRequestWhenUpdateRequestIsInvalid()
                throws Exception {

            String invalidRequest = """
                    {
                      "firstName": "",
                      "lastName": "",
                      "email": "invalid-email",
                      "phoneNumber": "123",
                      "passportNumber": "",
                      "nationality": "",
                      "dateOfBirth": null,
                      "gender": null
                    }
                    """;

            mockMvc.perform(
                            put("/api/passengers/{id}", PASSENGER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest)
                    )
                    .andExpect(status().isBadRequest());

            verify(passengerService, never())
                    .updatePassengerById(
                            anyLong(),
                            any(PassengerRequestDTO.class)
                    );
        }

        @Test
        @DisplayName("Should return 404 when updating missing passenger")
        void shouldReturnNotFoundWhenUpdatingPassenger()
                throws Exception {

            String message =
                    "Passenger not found with id: " + PASSENGER_ID;

            when(passengerService.updatePassengerById(
                    eq(PASSENGER_ID),
                    any(PassengerRequestDTO.class)
            )).thenThrow(new ResourceNotFoundException(message));

            mockMvc.perform(
                            put("/api/passengers/{id}", PASSENGER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(validPassengerJson())
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(message));

            verify(passengerService).updatePassengerById(
                    eq(PASSENGER_ID),
                    any(PassengerRequestDTO.class)
            );
        }

        @Test
        @DisplayName("Should return 409 when update has duplicate data")
        void shouldReturnConflictWhenUpdatingDuplicatePassenger()
                throws Exception {

            String message =
                    "Passenger already exists with email: " + EMAIL;

            when(passengerService.updatePassengerById(
                    eq(PASSENGER_ID),
                    any(PassengerRequestDTO.class)
            )).thenThrow(new DuplicateResourceException(message));

            mockMvc.perform(
                            put("/api/passengers/{id}", PASSENGER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(validPassengerJson())
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value(message));

            verify(passengerService).updatePassengerById(
                    eq(PASSENGER_ID),
                    any(PassengerRequestDTO.class)
            );
        }

        @Test
        @DisplayName("Should return 400 when update ID is invalid")
        void shouldReturnBadRequestWhenUpdateIdIsInvalid()
                throws Exception {

            mockMvc.perform(
                            put("/api/passengers/{id}", "abc")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(validPassengerJson())
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }

        @Test
        @DisplayName("Should return 400 when update body is missing")
        void shouldReturnBadRequestWhenUpdateBodyIsMissing()
                throws Exception {

            mockMvc.perform(
                            put("/api/passengers/{id}", PASSENGER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }

        @Test
        @DisplayName("Should return 400 when update date is in future")
        void shouldReturnBadRequestWhenUpdateDateIsInFuture()
                throws Exception {

            String futureDateRequest = """
                    {
                      "firstName": "Mahesh",
                      "lastName": "Karambalkar",
                      "email": "mahesh@example.com",
                      "phoneNumber": "9876543210",
                      "passportNumber": "P1234567",
                      "nationality": "Indian",
                      "dateOfBirth": "2099-01-14",
                      "gender": "MALE"
                    }
                    """;

            mockMvc.perform(
                            put("/api/passengers/{id}", PASSENGER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(futureDateRequest)
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }
    }

    private PassengerRequestDTO  createRequestDTO() {

        PassengerRequestDTO request = new PassengerRequestDTO();

        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setEmail(EMAIL);
        request.setPhoneNumber(PHONE_NUMBER);
        request.setPassportNumber(PASSPORT_NUMBER);
        request.setNationality(NATIONALITY);
        request.setDateOfBirth(DATE_OF_BIRTH);
        request.setGender(Gender.MALE);

        return request;
    }

    // =========================================================
    // DELETE PASSENGER
    // =========================================================
    @Nested
    @DisplayName("Delete Passenger API Tests")
    class DeletePassengerTests {

        @Test
        @DisplayName("Should delete passenger and return 204")
        void shouldDeletePassengerSuccessfully() throws Exception {

            mockMvc.perform(
                            delete("/api/passengers/{id}", PASSENGER_ID)
                    )
                    .andExpect(status().isNoContent());

            verify(passengerService, times(1))
                    .deletePassenger(PASSENGER_ID);
        }

        @Test
        @DisplayName("Should return 404 when deleting missing passenger")
        void shouldReturnNotFoundWhenDeletingPassenger()
                throws Exception {

            String message =
                    "Passenger not found with id: " + PASSENGER_ID;

            doThrow(new ResourceNotFoundException(message))
                    .when(passengerService)
                    .deletePassenger(PASSENGER_ID);

            mockMvc.perform(
                            delete("/api/passengers/{id}", PASSENGER_ID)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(message))
                    .andExpect(jsonPath("$.timeStamp").exists());

            verify(passengerService, times(1))
                    .deletePassenger(PASSENGER_ID);
        }

        @Test
        @DisplayName("Should return 400 when delete ID is invalid")
        void shouldReturnBadRequestWhenDeleteIdIsInvalid()
                throws Exception {

            mockMvc.perform(
                            delete("/api/passengers/{id}", "abc")
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passengerService);
        }
    }
    // =========================================================
    // TEST DATA
    // =========================================================

    private PassengerResponseDTO createResponseDTO() {

        PassengerResponseDTO response = new PassengerResponseDTO();

        response.setId(PASSENGER_ID);
        response.setFirstName(FIRST_NAME);
        response.setLastName(LAST_NAME);
        response.setEmail(EMAIL);
        response.setPhoneNumber(PHONE_NUMBER);
        response.setPassportNumber(PASSPORT_NUMBER);
        response.setNationality(NATIONALITY);
        response.setDateOfBirth(DATE_OF_BIRTH);
        response.setGender(Gender.MALE);
        response.setCreatedAt(
                LocalDateTime.of(2026, 7, 25, 10, 0)
        );
        response.setUpdatedAt(
                LocalDateTime.of(2026, 7, 25, 11, 0)
        );

        return response;
    }

    private PassengerResponseDTO createSecondResponseDTO() {

        PassengerResponseDTO response = new PassengerResponseDTO();

        response.setId(2L);
        response.setFirstName("Sonal");
        response.setLastName("Karambalkar");
        response.setEmail("sonal@example.com");
        response.setPhoneNumber("9876543211");
        response.setPassportNumber("P7654321");
        response.setNationality("Indian");
        response.setDateOfBirth(LocalDate.of(1999, 5, 10));
        response.setGender(Gender.FEMALE);
        response.setCreatedAt(
                LocalDateTime.of(2026, 7, 25, 10, 0)
        );
        response.setUpdatedAt(
                LocalDateTime.of(2026, 7, 25, 11, 0)
        );

        return response;
    }

    private String validPassengerJson() {

        return """
                {
                  "firstName": "Mahesh",
                  "lastName": "Karambalkar",
                  "email": "mahesh@example.com",
                  "phoneNumber": "9876543210",
                  "passportNumber": "P1234567",
                  "nationality": "Indian",
                  "dateOfBirth": "1998-01-14",
                  "gender": "MALE"
                }
                """;
    }
}