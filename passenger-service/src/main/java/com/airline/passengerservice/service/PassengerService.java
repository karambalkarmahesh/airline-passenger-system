package com.airline.passengerservice.service;

import com.airline.passengerservice.entity.Passenger;

import java.util.List;
import java.util.Optional;

public interface PassengerService {
    Passenger createPassenger(Passenger passenger);
    Passenger getPassengerById(Long id);
    List<Passenger> getAllPassengers();
    Passenger updatePassengerById( Long id,Passenger passenger);
    void deletePassenger(Long id);
}
