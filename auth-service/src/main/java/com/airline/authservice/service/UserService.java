package com.airline.authservice.service;

import com.airline.authservice.dto.LoginRequestDTO;
import com.airline.authservice.dto.LoginResponseDTO;
import com.airline.authservice.dto.RegisterRequestDTO;
import com.airline.authservice.dto.RegisterResponseDTO;

public interface UserService {

    RegisterResponseDTO register(RegisterRequestDTO request);

    LoginResponseDTO login(LoginRequestDTO request);
}