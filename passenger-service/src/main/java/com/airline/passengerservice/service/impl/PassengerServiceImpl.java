package com.airline.passengerservice.service.impl;

import com.airline.passengerservice.entity.Passenger;
import com.airline.passengerservice.exception.ResourceNotFoundException;
import com.airline.passengerservice.repository.PassengerRepository;
import com.airline.passengerservice.service.PassengerService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {


    private final PassengerRepository passengerRepository;

    public PassengerServiceImpl(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public Passenger createPassenger(Passenger passenger) {
        passenger.setCreatedAt(LocalDateTime.now());
        passenger.setUpdatedAt(LocalDateTime.now());
        return passengerRepository.save(passenger);
    }

    @Override
    public Passenger getPassengerById(Long id) {
        return passengerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "Passenger not found with id: "+id)
        );
    }

    @Override
    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    @Override
    public Passenger updatePassengerById(Long id, Passenger passenger) {
        Passenger existingPassenger = getPassengerById(id);
        existingPassenger.setEmail(passenger.getEmail());
        existingPassenger.setFirstName(passenger.getFirstName());
        existingPassenger.setLastName(passenger.getLastName());
        existingPassenger.setNationality(passenger.getNationality());
        existingPassenger.setDateOfBirth(passenger.getDateOfBirth());
        existingPassenger.setPassportNumber(passenger.getPassportNumber());
        existingPassenger.setPhoneNumber(passenger.getPhoneNumber());
        existingPassenger.setUpdatedAt(LocalDateTime.now());
        return passengerRepository.save(existingPassenger);
    }



    @Override
    public void deletePassenger(Long id) {
        Passenger passenger = getPassengerById(id);
        passengerRepository.delete(passenger);
    }
}
