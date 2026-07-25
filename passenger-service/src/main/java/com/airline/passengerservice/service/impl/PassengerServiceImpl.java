package com.airline.passengerservice.service.impl;

import com.airline.passengerservice.dto.PassengerRequestDTO;
import com.airline.passengerservice.dto.PassengerResponseDTO;
import com.airline.passengerservice.entity.Passenger;
import com.airline.passengerservice.exception.DuplicateResourceException;
import com.airline.passengerservice.exception.ResourceNotFoundException;
import com.airline.passengerservice.mapper.PassengerMapper;
import com.airline.passengerservice.repository.PassengerRepository;
import com.airline.passengerservice.service.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {


    private final Logger log = LoggerFactory.getLogger(PassengerServiceImpl.class);
    private final PassengerRepository passengerRepository;

    private final PassengerMapper passengerMapper;



    public PassengerServiceImpl(PassengerRepository passengerRepository, PassengerMapper passengerMapper) {
        this.passengerRepository = passengerRepository;
        this.passengerMapper = passengerMapper;
    }

    @Override
    public PassengerResponseDTO createPassenger(PassengerRequestDTO request) {

        log.info(
                "Creating passenger with email: {}",
                request.getEmail()
        );
        if(passengerRepository.existsByPassportNumber(request.getPassportNumber())){

            log.warn("passenger creation failed because Passport number already exists: {}",request.getPassportNumber());

            throw new DuplicateResourceException(
                    "Passenger already exists with passport number: "
                            + request.getPassportNumber()
            );
        }
        if(passengerRepository.existsByEmail(request.getEmail())){

            log.warn("passenger creation failed because Email already exists: {}",request.getEmail());

            throw new DuplicateResourceException(
                    "Passenger already exists with email: "
                            + request.getEmail()
            );
        }
        Passenger passenger = passengerMapper.toEntity(request);

        passenger.setCreatedAt(LocalDateTime.now());
        passenger.setUpdatedAt(LocalDateTime.now());
        Passenger savedPassenger = passengerRepository.save(passenger);

        log.info(
                "Passenger created successfully with ID: {}",
                savedPassenger.getId()
        );

        return passengerMapper.toResponseDTO(savedPassenger);
    }

    @Override
    public PassengerResponseDTO getPassengerById(Long id) {

        log.debug("Fetching passenger with ID: {}", id);

        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn("Passenger not found with ID: {}", id);

                    return new ResourceNotFoundException(
                            "Passenger not found with ID: " + id
                    );
                });

        log.debug("Passenger retrieved successfully with ID: {}", id);

        return passengerMapper.toResponseDTO(passenger);
    }

    @Override
    public List<PassengerResponseDTO> getAllPassengers() {

        List<Passenger> allPassenger = passengerRepository.findAll();

        return passengerMapper.toResponseDTO(allPassenger);
    }

    @Override
    public PassengerResponseDTO updatePassengerById(Long id, PassengerRequestDTO request) {

        log.info("Updating passenger with ID: {}", id);

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
        log.info("Passenger updated successfully with ID: {}", id);
        // Convert Entity to Response DTO
        return passengerMapper.toResponseDTO(updatedPassenger);
    }



    @Override
    public void deletePassenger(Long id) {
        log.info("Deleting passenger with ID: {}", id);
        Passenger  existingPassenger= passengerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Passenger not found with id: " + id));

        passengerRepository.delete(existingPassenger);
        log.info("Passenger deleted successfully with ID: {}", id);
    }
}
