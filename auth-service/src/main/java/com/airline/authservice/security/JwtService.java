package com.airline.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Convert configured secret into signing key.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generate JWT token after successful login.
     */
    public String generateToken(
            String email,
            String role
    ) {

        Date now = new Date();

        Date expiryDate =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Parse token and return all claims.
     *
     * Signature is also verified here.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Email is stored inside JWT subject.
     */
    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    /**
     * Extract USER / ADMIN role from JWT.
     */
    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    /**
     * Get token expiration date.
     */
    public Date extractExpiration(String token) {

        return extractAllClaims(token)
                .getExpiration();
    }

    /**
     * Check whether token has expired.
     */
    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    /**
     * Validate JWT.
     *
     * Checks:
     * 1. Signature
     * 2. Token format
     * 3. Expiration
     */
    public boolean isTokenValid(String token) {

        try {

            extractAllClaims(token);

            return !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException exception) {

            return false;
        }
    }
}