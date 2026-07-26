package com.airline.passengerservice.integration;

import com.airline.passengerservice.entity.Passenger;
import com.airline.passengerservice.enums.Gender;
import com.airline.passengerservice.exception.DuplicateResourceException;
import com.airline.passengerservice.repository.PassengerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PassengerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PassengerRepository passengerRepository;

    @BeforeEach
    void setUp() {
        passengerRepository.deleteAll();
    }

    private Passenger createPassenger() {

        Passenger passenger = new Passenger();

        passenger.setFirstName("Mahesh");
        passenger.setLastName("Karambalkar");
        passenger.setEmail("mahesh@test.com");
        passenger.setPhoneNumber("9876543210");
        passenger.setPassportNumber("P12345678");
        passenger.setNationality("Indian");
        passenger.setDateOfBirth(LocalDate.of(1998, 1, 14));
        passenger.setGender(Gender.MALE);

        return passenger;
    }

    @Test
    @DisplayName("Should create passenger successfully")
    void shouldCreatePassengerSuccessfully() throws Exception {

        String requestJson = """
            {
              "firstName": "Mahesh",
              "lastName": "Karambalkar",
              "email": "mahesh.integration@test.com",
              "phoneNumber": "9876543210",
              "passportNumber": "P12345678",
              "nationality": "Indian",
              "dateOfBirth": "1998-01-14",
              "gender": "MALE"
            }
            """;

        mockMvc.perform(
                        post("/api/passengers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Mahesh"))
                .andExpect(jsonPath("$.lastName").value("Karambalkar"))
                .andExpect(jsonPath("$.email")
                        .value("mahesh.integration@test.com"))
                .andExpect(jsonPath("$.passportNumber")
                        .value("P12345678"))
                .andExpect(jsonPath("$.gender").value("MALE"));
    }

    @Test
    @DisplayName("Should get passenger by id")
    void shouldGetPassengerById() throws Exception {

        Passenger passenger = createPassenger();
        passenger = passengerRepository.save(passenger);

        mockMvc.perform(get("/api/passengers/{id}", passenger.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(passenger.getId()))
                .andExpect(jsonPath("$.firstName").value("Mahesh"))
                .andExpect(jsonPath("$.email").value("mahesh@test.com"));
    }

    @Test
    @DisplayName("Should return all passengers")
    void shouldReturnAllPassengers() throws Exception {

        passengerRepository.save(createPassenger());

        Passenger passenger2 = createPassenger();
        passenger2.setEmail("second@test.com");
        passenger2.setPassportNumber("P98765432");
        passenger2.setPhoneNumber("9876543211");

        passengerRepository.save(passenger2);

        mockMvc.perform(get("/api/passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    @DisplayName("Should update passenger successfully")
    void shouldUpdatePassengerSuccessfully() throws Exception {

        Passenger passenger = passengerRepository.save(createPassenger());

        String updateRequest = """
            {
              "firstName": "Mahesh Updated",
              "lastName": "Karambalkar",
              "email": "mahesh.updated@test.com",
              "phoneNumber": "9999999999",
              "passportNumber": "P87654321",
              "nationality": "Indian",
              "dateOfBirth": "1998-01-14",
              "gender": "MALE"
            }
            """;

        mockMvc.perform(put("/api/passengers/{id}", passenger.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Mahesh Updated"))
                .andExpect(jsonPath("$.email").value("mahesh.updated@test.com"))
                .andExpect(jsonPath("$.passportNumber").value("P87654321"));

        Passenger updated = passengerRepository.findById(passenger.getId()).orElseThrow();

        assertThat(updated.getFirstName()).isEqualTo("Mahesh Updated");
        assertThat(updated.getEmail()).isEqualTo("mahesh.updated@test.com");
        assertThat(updated.getPassportNumber()).isEqualTo("P87654321");
    }

    @Test
    @DisplayName("Should return conflict when email already exists")
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

        passengerRepository.save(createPassenger());

        String request = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "mahesh@test.com",
              "phoneNumber": "9999999999",
              "passportNumber": "P99999999",
              "nationality": "Indian",
              "dateOfBirth": "1995-01-01",
              "gender": "MALE"
            }
            """;

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Passenger already exists with email: mahesh@test.com"));
    }

    @Test
    @DisplayName("Should return conflict when passport number already exists")
    void shouldReturnConflictWhenPassportAlreadyExists() throws Exception {

        passengerRepository.save(createPassenger());

        String request = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "john@test.com",
              "phoneNumber": "9999999999",
              "passportNumber": "P12345678",
              "nationality": "Indian",
              "dateOfBirth": "1995-01-01",
              "gender": "MALE"
            }
            """;

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Passenger already exists with passport number: P12345678"));
    }

    @Test
    @DisplayName("Should delete passenger successfully")
    void shouldDeletePassengerSuccessfully() throws Exception {

        Passenger passenger = passengerRepository.save(createPassenger());

        mockMvc.perform(delete("/api/passengers/{id}", passenger.getId()))
                .andExpect(status().isNoContent());

        assertThat(passengerRepository.findById(passenger.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should return bad request for invalid input")
    void shouldReturnBadRequestForInvalidInput() throws Exception {

        String request = """
            {
              "firstName": "",
              "email": "invalid-email"
            }
            """;

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return not found when passenger does not exist")
    void shouldReturnNotFoundWhenPassengerDoesNotExist() throws Exception {

        mockMvc.perform(get("/api/passengers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Passenger not found with ID: 999"));
    }
}