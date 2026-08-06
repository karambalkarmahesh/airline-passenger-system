package com.airline.authservice.dto;
import com.airline.authservice.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private LocalDateTime createdAt;
}