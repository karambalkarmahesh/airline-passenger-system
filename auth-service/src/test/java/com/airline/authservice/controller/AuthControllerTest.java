package com.airline.authservice.controller;

import com.airline.authservice.dto.LoginRequestDTO;
import com.airline.authservice.dto.LoginResponseDTO;
import com.airline.authservice.dto.RegisterRequestDTO;
import com.airline.authservice.dto.RegisterResponseDTO;
import com.airline.authservice.enums.Role;
import com.airline.authservice.exception.DuplicateResourceException;
import com.airline.authservice.exception.GlobalExceptionHandler;
import com.airline.authservice.exception.InvalidCredentialsException;
import com.airline.authservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        AuthController controller =
                new AuthController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldRegisterUserSuccessfully()
            throws Exception {

        RegisterResponseDTO response =
                RegisterResponseDTO.builder()
                        .id(1L)
                        .firstName("Mahesh")
                        .lastName("Karambalkar")
                        .email("mahesh@example.com")
                        .role(Role.USER)
                        .createdAt(LocalDateTime.now())
                        .build();

        when(userService.register(
                any(RegisterRequestDTO.class)
        )).thenReturn(response);

        String request = """
                {
                  "firstName":"Mahesh",
                  "lastName":"Karambalkar",
                  "email":"mahesh@example.com",
                  "password":"Password@123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.email")
                        .value("mahesh@example.com"))
                .andExpect(jsonPath("$.role")
                        .value("USER"));

        verify(userService)
                .register(any(RegisterRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidRegistration()
            throws Exception {

        String request = """
                {
                  "firstName":"",
                  "lastName":"",
                  "email":"wrong-email",
                  "password":"123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnConflictForDuplicateEmail()
            throws Exception {

        when(userService.register(
                any(RegisterRequestDTO.class)
        )).thenThrow(
                new DuplicateResourceException(
                        "User already exists with email: mahesh@example.com"
                )
        );

        String request = """
                {
                  "firstName":"Mahesh",
                  "lastName":"Karambalkar",
                  "email":"mahesh@example.com",
                  "password":"Password@123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409));
    }

    @Test
    void shouldLoginSuccessfully()
            throws Exception {

        LoginResponseDTO response =
                LoginResponseDTO.builder()
                        .id(1L)
                        .firstName("Mahesh")
                        .lastName("Karambalkar")
                        .email("mahesh@example.com")
                        .role(Role.USER)
                        .accessToken("jwt-token")
                        .tokenType("Bearer")
                        .build();

        when(userService.login(
                any(LoginRequestDTO.class)
        )).thenReturn(response);

        String request = """
                {
                  "email":"mahesh@example.com",
                  "password":"Password@123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("jwt-token"))
                .andExpect(jsonPath("$.tokenType")
                        .value("Bearer"))
                .andExpect(jsonPath("$.role")
                        .value("USER"));
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials()
            throws Exception {

        when(userService.login(
                any(LoginRequestDTO.class)
        )).thenThrow(
                new InvalidCredentialsException(
                        "Invalid email or password"
                )
        );

        String request = """
                {
                  "email":"mahesh@example.com",
                  "password":"WrongPassword"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status")
                        .value(401))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"));
    }

    @Test
    void shouldReturnBadRequestForInvalidLoginRequest()
            throws Exception {

        String request = """
                {
                  "email":"wrong-email",
                  "password":""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}