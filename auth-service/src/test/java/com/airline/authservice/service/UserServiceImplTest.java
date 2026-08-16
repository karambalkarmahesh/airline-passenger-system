package com.airline.authservice.service;

import com.airline.authservice.dto.LoginRequestDTO;
import com.airline.authservice.dto.LoginResponseDTO;
import com.airline.authservice.dto.RegisterRequestDTO;
import com.airline.authservice.dto.RegisterResponseDTO;
import com.airline.authservice.entity.User;
import com.airline.authservice.enums.Role;
import com.airline.authservice.exception.DuplicateResourceException;
import com.airline.authservice.exception.InvalidCredentialsException;
import com.airline.authservice.mapper.UserMapper;
import com.airline.authservice.repository.UserRepository;
import com.airline.authservice.security.JwtService;
import com.airline.authservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequestDTO registerRequest;
    private User user;

    @BeforeEach
    void setUp() {

        registerRequest = RegisterRequestDTO.builder()
                .firstName("Mahesh")
                .lastName("Karambalkar")
                .email("mahesh@example.com")
                .password("Password@123")
                .build();

        user = User.builder()
                .id(1L)
                .firstName("Mahesh")
                .lastName("Karambalkar")
                .email("mahesh@example.com")
                .password("$2a$10$encoded")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        when(userRepository.existsByEmail("mahesh@example.com"))
                .thenReturn(false);

        when(userMapper.toEntity(registerRequest))
                .thenReturn(user);

        when(passwordEncoder.encode("Password@123"))
                .thenReturn("$2a$10$encoded");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        RegisterResponseDTO response = RegisterResponseDTO.builder()
                .id(1L)
                .firstName("Mahesh")
                .lastName("Karambalkar")
                .email("mahesh@example.com")
                .role(Role.USER)
                .createdAt(user.getCreatedAt())
                .build();

        when(userMapper.toResponse(user))
                .thenReturn(response);

        RegisterResponseDTO result =
                userService.register(registerRequest);

        assertNotNull(result);
        assertEquals("mahesh@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());

        verify(passwordEncoder).encode("Password@123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail("mahesh@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.register(registerRequest)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("mahesh@example.com")
                .password("Password@123")
                .build();

        when(userRepository.findByEmail("mahesh@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "Password@123",
                user.getPassword()
        )).thenReturn(true);

        when(jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        )).thenReturn("jwt-token");

        LoginResponseDTO result =
                userService.login(request);

        assertNotNull(result);
        assertEquals("jwt-token", result.getAccessToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("unknown@example.com")
                .password("Password@123")
                .build();

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("mahesh@example.com")
                .password("WrongPassword")
                .build();

        when(userRepository.findByEmail("mahesh@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                user.getPassword()
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(jwtService, never())
                .generateToken(anyString(), anyString());
    }
}