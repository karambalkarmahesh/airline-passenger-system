package com.airline.authservice.mapper;

import com.airline.authservice.dto.RegisterRequestDTO;
import com.airline.authservice.dto.RegisterResponseDTO;
import com.airline.authservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(RegisterRequestDTO dto);

    RegisterResponseDTO toResponse(User user);
}