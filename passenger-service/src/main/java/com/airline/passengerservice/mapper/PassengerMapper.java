package com.airline.passengerservice.mapper;

import com.airline.passengerservice.dto.PassengerRequestDTO;
import com.airline.passengerservice.dto.PassengerResponseDTO;
import com.airline.passengerservice.entity.Passenger;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    Passenger toEntity(PassengerRequestDTO request);

    PassengerResponseDTO toResponseDTO(Passenger passenger);

    List<PassengerResponseDTO> toResponseDTO(List<Passenger> passengers);
}