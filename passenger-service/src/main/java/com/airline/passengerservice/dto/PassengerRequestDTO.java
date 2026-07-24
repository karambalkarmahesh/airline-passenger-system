package com.airline.passengerservice.dto;

import com.airline.passengerservice.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email")
    private String email;
    private String phoneNumber;
    private String passportNumber;
    private String nationality;
    private LocalDate dateOfBirth;
    private Gender gender;
}