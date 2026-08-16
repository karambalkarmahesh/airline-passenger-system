package com.airline.authservice.integration;

import com.airline.authservice.entity.User;
import com.airline.authservice.enums.Role;
import com.airline.authservice.repository.UserRepository;
import com.airline.authservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private User createUser(
            String email,
            String rawPassword,
            Role role
    ) {
        User user = User.builder()
                .firstName("Mahesh")
                .lastName("Karambalkar")
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();

        return userRepository.save(user);
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {

        String request = """
                {
                  "firstName": "Mahesh",
                  "lastName": "Karambalkar",
                  "email": "mahesh@test.com",
                  "password": "Password@123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email")
                        .value("mahesh@test.com"))
                .andExpect(jsonPath("$.role")
                        .value("USER"));

        assertThat(
                userRepository.existsByEmail("mahesh@test.com")
        ).isTrue();
    }

    @Test
    @DisplayName("Should return conflict for duplicate registration")
    void shouldReturnConflictForDuplicateRegistration() throws Exception {

        createUser(
                "mahesh@test.com",
                "Password@123",
                Role.USER
        );

        String request = """
                {
                  "firstName": "Mahesh",
                  "lastName": "Karambalkar",
                  "email": "mahesh@test.com",
                  "password": "Password@123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Should login successfully and return JWT")
    void shouldLoginSuccessfullyAndReturnJwt() throws Exception {

        createUser(
                "mahesh@test.com",
                "Password@123",
                Role.USER
        );

        String request = """
                {
                  "email": "mahesh@test.com",
                  "password": "Password@123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType")
                        .value("Bearer"))
                .andExpect(jsonPath("$.role")
                        .value("USER"));
    }

    @Test
    @DisplayName("Should return unauthorized for wrong password")
    void shouldReturnUnauthorizedForWrongPassword() throws Exception {

        createUser(
                "mahesh@test.com",
                "Password@123",
                Role.USER
        );

        String request = """
                {
                  "email": "mahesh@test.com",
                  "password": "WrongPassword"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return unauthorized without JWT")
    void shouldReturnUnauthorizedWithoutJwt() throws Exception {

        mockMvc.perform(
                        get("/api/v1/auth/profile")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return unauthorized for invalid JWT")
    void shouldReturnUnauthorizedForInvalidJwt() throws Exception {

        mockMvc.perform(
                        get("/api/v1/auth/profile")
                                .header(
                                        "Authorization",
                                        "Bearer invalid.jwt.token"
                                )
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("USER should access profile")
    void userShouldAccessProfile() throws Exception {

        User user = createUser(
                "user@test.com",
                "Password@123",
                Role.USER
        );

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        mockMvc.perform(
                        get("/api/v1/auth/profile")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER should not access admin endpoint")
    void userShouldNotAccessAdminEndpoint() throws Exception {

        User user = createUser(
                "user@test.com",
                "Password@123",
                Role.USER
        );

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        mockMvc.perform(
                        get("/api/v1/auth/admin")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN should access admin endpoint")
    void adminShouldAccessAdminEndpoint() throws Exception {

        User admin = createUser(
                "admin@test.com",
                "Password@123",
                Role.ADMIN
        );

        String token = jwtService.generateToken(
                admin.getEmail(),
                admin.getRole().name()
        );

        mockMvc.perform(
                        get("/api/v1/auth/admin")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }
}