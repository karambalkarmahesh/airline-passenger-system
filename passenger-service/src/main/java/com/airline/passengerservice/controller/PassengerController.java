package com.airline.passengerservice.controller;

import com.airline.passengerservice.dto.PassengerRequestDTO;
import com.airline.passengerservice.dto.PassengerResponseDTO;
import com.airline.passengerservice.entity.Passenger;
import com.airline.passengerservice.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    public ResponseEntity<PassengerResponseDTO> createPassenger(@Valid @RequestBody PassengerRequestDTO passenger){
       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(passengerService.createPassenger(passenger));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> getPassengerById(@PathVariable("id") Long id){
        PassengerResponseDTO passenger = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passenger);
    }

    @GetMapping
    public ResponseEntity<List<PassengerResponseDTO>> getAllPassengers(){
        return ResponseEntity.ok(passengerService.getAllPassengers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> updatePassenger(@PathVariable("id") Long id,@Valid @RequestBody PassengerRequestDTO request){
        return ResponseEntity.ok(passengerService.updatePassengerById(id, request));
        }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePassenger(@PathVariable("id") Long id){
        passengerService.deletePassenger(id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted passenger with id: "+id);
    }
}
