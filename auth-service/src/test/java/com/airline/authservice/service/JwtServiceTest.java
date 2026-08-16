package com.airline.authservice.service;

import com.airline.authservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "my-super-secret-key-for-airline-auth-service-123456";

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                SECRET
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                3600000L
        );
    }

    @Test
    void shouldGenerateToken() {

        String token =
                jwtService.generateToken(
                        "mahesh@example.com",
                        "USER"
                );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractEmailFromToken() {

        String token =
                jwtService.generateToken(
                        "mahesh@example.com",
                        "USER"
                );

        String email = jwtService.extractEmail(token);

        assertEquals(
                "mahesh@example.com",
                email
        );
    }

    @Test
    void shouldExtractRoleFromToken() {

        String token =
                jwtService.generateToken(
                        "mahesh@example.com",
                        "ADMIN"
                );

        String role = jwtService.extractRole(token);

        assertEquals("ADMIN", role);
    }

    @Test
    void shouldReturnTrueForValidToken() {

        String token =
                jwtService.generateToken(
                        "mahesh@example.com",
                        "USER"
                );

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {

        assertFalse(
                jwtService.isTokenValid("invalid.jwt.token")
        );
    }

    @Test
    void shouldReturnFalseForExpiredToken() {

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                -1000L
        );

        String token =
                jwtService.generateToken(
                        "mahesh@example.com",
                        "USER"
                );

        assertFalse(jwtService.isTokenValid(token));
    }
}