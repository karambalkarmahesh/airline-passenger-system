package com.airline.authservice.controller;

import com.airline.authservice.dto.LoginRequestDTO;
import com.airline.authservice.dto.LoginResponseDTO;
import com.airline.authservice.dto.RegisterRequestDTO;
import com.airline.authservice.dto.RegisterResponseDTO;
import com.airline.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        RegisterResponseDTO response = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO){

        return ResponseEntity.ok(userService.login(requestDTO));
    }

    @GetMapping("/profile")
    public ResponseEntity<String> profile() {
        return ResponseEntity.ok("JWT authentication successful");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Admin access successful");
    }
}