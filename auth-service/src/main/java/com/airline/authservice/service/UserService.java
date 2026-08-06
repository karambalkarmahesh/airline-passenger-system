package com.airline.authservice.service;

import com.airline.authservice.dto.RegisterRequestDTO;
import com.airline.authservice.dto.RegisterResponseDTO;

public interface UserService {

    RegisterResponseDTO register(RegisterRequestDTO request);

}