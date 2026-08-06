package com.airline.authservice.service.impl;

import com.airline.authservice.dto.RegisterRequestDTO;
import com.airline.authservice.dto.RegisterResponseDTO;
import com.airline.authservice.entity.User;
import com.airline.authservice.enums.Role;
import com.airline.authservice.exception.DuplicateResourceException;
import com.airline.authservice.mapper.UserMapper;
import com.airline.authservice.repository.UserRepository;
import com.airline.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "User already exists with email: " + email
            );
        }

        request.setEmail(email);

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}