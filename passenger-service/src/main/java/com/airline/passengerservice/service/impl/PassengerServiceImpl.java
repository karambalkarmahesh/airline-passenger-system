package com.airline.passengerservice.service.impl;

import com.airline.passengerservice.dto.PassengerRequestDTO;
import com.airline.passengerservice.dto.PassengerResponseDTO;
import com.airline.passengerservice.entity.Passenger;
import com.airline.passengerservice.exception.DuplicateResourceException;
import com.airline.passengerservice.exception.ResourceNotFoundException;
import com.airline.passengerservice.mapper.PassengerMapper;
import com.airline.passengerservice.repository.PassengerRepository;
import com.airline.passengerservice.service.PassengerService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {


    private final PassengerRepository passengerRepository;

    private final PassengerMapper passengerMapper;



    public PassengerServiceImpl(PassengerRepository passengerRepository, PassengerMapper passengerMapper) {
        this.passengerRepository = passengerRepository;
        this.passengerMapper = passengerMapper;
    }

    @Override
    public PassengerResponseDTO createPassenger(PassengerRequestDTO request) {
        if(passengerRepository.existsByPassportNumber(request.getPassportNumber())){
            throw new DuplicateResourceException(
                    "Passenger already exists with passport number: "
                            + request.getPassportNumber()
            );
        }
        if(passengerRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException(
                    "Passenger already exists with email: "
                            + request.getEmail()
            );
        }
        Passenger passenger = passengerMapper.toEntity(request);

        passenger.setCreatedAt(LocalDateTime.now());
        passenger.setUpdatedAt(LocalDateTime.now());
        Passenger savedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toResponseDTO(savedPassenger);
    }

    @Override
    public PassengerResponseDTO getPassengerById(Long id) {

        Passenger passenger = passengerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "Passenger not found with id: " + id)
        );
        return passengerMapper.toResponseDTO(passenger);
    }

    @Override
    public List<PassengerResponseDTO> getAllPassengers() {

        List<Passenger> allPassenger = passengerRepository.findAll();

        return passengerMapper.toResponseDTO(allPassenger);
    }

    @Override
    public PassengerResponseDTO updatePassengerById(Long id, PassengerRequestDTO request) {

        // Fetch existing passenger from database
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Passenger not found with id: " + id));


        if (passengerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Passenger already exists with email: " + request.getEmail()
            );
        }

        if (passengerRepository.existsByPassportNumber(
                request.getPassportNumber())) {

            throw new DuplicateResourceException(
                    "Passenger already exists with passport number: "
                            + request.getPassportNumber()
            );
        }
        // Update fields
        passengerMapper.updatePassengerFromRequest(
                request,
                existingPassenger
        );

        // Update audit field
        existingPassenger.setUpdatedAt(LocalDateTime.now());

        // Save updated entity
        Passenger updatedPassenger = passengerRepository.save(existingPassenger);

        // Convert Entity to Response DTO
        return passengerMapper.toResponseDTO(updatedPassenger);
    }



    @Override
    public void deletePassenger(Long id) {
        Passenger  existingPassenger= passengerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Passenger not found with id: " + id));

        passengerRepository.delete(existingPassenger);
    }
}
