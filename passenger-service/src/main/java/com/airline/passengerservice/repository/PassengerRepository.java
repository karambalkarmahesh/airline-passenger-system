package com.airline.passengerservice.repository;

import com.airline.passengerservice.entity.Passenger;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Long> {
    boolean existsByPassportNumber(String passportNumber);

    boolean existsByEmail(@Email(message = "Invalid email") String email);
}
