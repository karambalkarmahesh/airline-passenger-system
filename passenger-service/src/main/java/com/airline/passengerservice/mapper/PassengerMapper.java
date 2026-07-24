package com.airline.passengerservice.mapper;

import com.airline.passengerservice.dto.PassengerRequestDTO;
import com.airline.passengerservice.dto.PassengerResponseDTO;
import com.airline.passengerservice.entity.Passenger;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    Passenger toEntity(PassengerRequestDTO request);

    PassengerResponseDTO toResponseDTO(Passenger passenger);

    List<PassengerResponseDTO> toResponseDTO(List<Passenger> passengers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePassengerFromRequest(
            PassengerRequestDTO request,
            @MappingTarget Passenger passenger);

}