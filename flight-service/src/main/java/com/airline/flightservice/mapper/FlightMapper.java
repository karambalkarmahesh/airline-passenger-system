package com.airline.flightservice.mapper;

import com.airline.flightservice.dto.FlightRequestDTO;
import com.airline.flightservice.dto.FlightResponseDTO;
import com.airline.flightservice.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FlightMapper {

    Flight toEntity(FlightRequestDTO request);

    FlightResponseDTO toResponseDTO(Flight flight);

    void updateFlightFromRequest(
            FlightRequestDTO request,
            @MappingTarget Flight flight
    );
}