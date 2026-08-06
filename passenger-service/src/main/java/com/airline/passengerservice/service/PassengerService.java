package com.airline.passengerservice.service;

import com.airline.passengerservice.dto.PassengerRequestDTO;
import com.airline.passengerservice.dto.PassengerResponseDTO;
import com.airline.passengerservice.entity.Passenger;

import java.util.List;
import java.util.Optional;

public interface PassengerService {
    PassengerResponseDTO createPassenger(PassengerRequestDTO passenger);
    PassengerResponseDTO  getPassengerById(Long id);
    List<PassengerResponseDTO> getAllPassengers();
    PassengerResponseDTO  updatePassengerById( Long id,PassengerRequestDTO passenger);
    void deletePassenger(Long id);
}
