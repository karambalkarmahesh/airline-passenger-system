package com.airline.authservice.dto;

import com.airline.authservice.enums.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String message;
}
