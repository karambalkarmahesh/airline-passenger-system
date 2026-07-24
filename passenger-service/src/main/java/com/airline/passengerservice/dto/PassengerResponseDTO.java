package com.airline.passengerservice.dto;

import com.airline.passengerservice.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String passportNumber;
    private String nationality;
    private LocalDate dateOfBirth;
    private Gender gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}