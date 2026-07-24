package com.airline.passengerservice.controller;

import com.airline.passengerservice.dto.PassengerRequestDTO;
import com.airline.passengerservice.dto.PassengerResponseDTO;
import com.airline.passengerservice.entity.Passenger;
import com.airline.passengerservice.service.PassengerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@Tag(
        name = "Passenger Management",
        description = "APIs for managing passenger information"
)
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @Operation(
            summary = "Create Passenger",
            description = "Creates a new passenger in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Passenger created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Duplicate passenger")
    })
    @PostMapping
    public ResponseEntity<PassengerResponseDTO> createPassenger(@Valid @RequestBody PassengerRequestDTO passenger){
       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(passengerService.createPassenger(passenger));
    }

    @Operation(
            summary = "Get passenger by ID",
            description = "Returns passenger details for the provided passenger ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Passenger retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Passenger not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> getPassengerById(@PathVariable("id") Long id){
        PassengerResponseDTO passenger = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passenger);
    }


    @Operation(
            summary = "Get all passengers",
            description = "Returns all passengers available in the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Passengers retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<PassengerResponseDTO>> getAllPassengers(){
        return ResponseEntity.ok(passengerService.getAllPassengers());
    }

    @Operation(
            summary = "Update a passenger",
            description = "Updates an existing passenger using the provided passenger ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Passenger updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid passenger request"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Passenger not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email or passport number already exists"
            )
    })

    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> updatePassenger(@PathVariable("id") Long id,@Valid @RequestBody PassengerRequestDTO request){
        return ResponseEntity.ok(passengerService.updatePassengerById(id, request));
        }

    @Operation(
            summary = "Delete a passenger",
            description = "Deletes an existing passenger using the provided passenger ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Passenger deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Passenger not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePassenger(@PathVariable("id") Long id){
        passengerService.deletePassenger(id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted passenger with id: "+id);
    }
}
