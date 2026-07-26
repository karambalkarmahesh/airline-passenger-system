package com.airline.passengerservice.repository;

import com.airline.passengerservice.entity.Passenger;
import com.airline.passengerservice.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test")
class PassengerRepositoryTest {

    @Autowired
    private PassengerRepository repository;

    private Passenger createPassenger() {
        Passenger passenger = new Passenger();

        passenger.setFirstName("Mahesh");
        passenger.setLastName("Karambalkar");
        passenger.setEmail("mahesh@test.com");
        passenger.setPhoneNumber("9876543210");
        passenger.setPassportNumber("P12345678");
        passenger.setNationality("Indian");
        passenger.setGender(Gender.MALE);
        passenger.setDateOfBirth(LocalDate.of(1998,1,14));

        return passenger;
    }

    @Test
    void shouldSavePassenger() {

        Passenger passenger = createPassenger();

        Passenger saved = repository.save(passenger);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should find passenger by ID")
    void shouldFindPassengerById() {

        Passenger savedPassenger =
                repository.save(createPassenger());

        Optional<Passenger> result =
                repository.findById(savedPassenger.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Mahesh");
        assertThat(result.get().getEmail()).isEqualTo("mahesh@test.com");
    }

    @Test
    @DisplayName("Should return empty when passenger ID does not exist")
    void shouldReturnEmptyWhenPassengerNotFound() {

        Optional<Passenger> result =
                repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return true when email exists")
    void shouldReturnTrueWhenEmailExists() {

        repository.save(createPassenger());

        boolean exists =
                repository.existsByEmail("mahesh@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void shouldReturnFalseWhenEmailDoesNotExist() {

        boolean exists =
                repository.existsByEmail("unknown@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return true when passport number exists")
    void shouldReturnTrueWhenPassportNumberExists() {

        repository.save(createPassenger());

        boolean exists =
                repository.existsByPassportNumber("P12345678");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return all passengers")
    void shouldReturnAllPassengers() {

        Passenger passengerOne = createPassenger();

        Passenger passengerTwo = createPassenger();
        passengerTwo.setEmail("second@test.com");
        passengerTwo.setPhoneNumber("9876543211");
        passengerTwo.setPassportNumber("P87654321");

        repository.save(passengerOne);
        repository.save(passengerTwo);

        List<Passenger> passengers =
                repository.findAll();

        assertThat(passengers).hasSize(2);
    }

    @Test
    @DisplayName("Should delete passenger")
    void shouldDeletePassenger() {

        Passenger savedPassenger =
                repository.save(createPassenger());

        repository.deleteById(savedPassenger.getId());

        Optional<Passenger> result =
                repository.findById(savedPassenger.getId());

        assertThat(result).isEmpty();
    }
}