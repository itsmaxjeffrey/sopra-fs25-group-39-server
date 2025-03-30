package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.ContractDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.ContractService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.time.LocalDateTime;

@RestController
public class ContractController {

    private final ContractService contractService;
    private final LocationService locationService;

    public ContractController(ContractService contractService, LocationService locationService) {
        this.contractService = contractService;
        this.locationService = locationService;
    }

    /**
     * Create a new contract
     * @param contractPostDTO the contract data
     * @return the created contract
     */
    @PostMapping("/api/v1/contracts")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ContractGetDTO createContract(@RequestBody ContractPostDTO contractPostDTO) {
        validateContractPostDTO(contractPostDTO);

        // Create locations first
        Location fromLocation = DTOMapper.INSTANCE.convertLocationDTOtoEntity(contractPostDTO.getFromLocation());
        Location toLocation = DTOMapper.INSTANCE.convertLocationDTOtoEntity(contractPostDTO.getToLocation());

        fromLocation = locationService.createLocation(fromLocation);
        toLocation = locationService.createLocation(toLocation);

        // Convert DTO to entity
        Contract contractInput = ContractDTOMapper.INSTANCE.convertContractPostDTOtoEntity(contractPostDTO);
        contractInput.setFromAddress(fromLocation);
        contractInput.setToAddress(toLocation);

        // Create contract
        Contract createdContract = contractService.createContract(contractInput);

        return ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(createdContract);
    }

    /**
     * Validate the required fields in contract post DTO
     */
    private void validateContractPostDTO(ContractPostDTO contractPostDTO) {
            // Check if from and to locations are provided
            if (contractPostDTO.getFromLocation() == null || contractPostDTO.getToLocation() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From and to locations are required");
            }
        
            // Check if from location has valid latitude and longitude
            if (contractPostDTO.getFromLocation().getLatitude() == null || contractPostDTO.getFromLocation().getLongitude() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From location must have a valid latitude and longitude");
            } 
        
            // Check if to location has valid latitude and longitude
            if (contractPostDTO.getToLocation().getLatitude() == null || contractPostDTO.getToLocation().getLongitude() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "To location must have a valid latitude and longitude");
            }
        
            // Check if from and to locations are the same  
            if (contractPostDTO.getFromLocation().getLatitude() == contractPostDTO.getToLocation().getLatitude() && 
                contractPostDTO.getFromLocation().getLongitude() == contractPostDTO.getToLocation().getLongitude()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From and to locations cannot be the same");
            }
        
            // Check if requester is provided
            if (contractPostDTO.getRequesterId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requester ID is required");
            }
        
            // Check if title is provided and not empty
            if (contractPostDTO.getTitle() == null || contractPostDTO.getTitle().trim().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
            }
        
            // Check if move date time is provided and in the future
            if (contractPostDTO.getMoveDateTime() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Move date time is required");
            }
            if (contractPostDTO.getMoveDateTime().isBefore(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Move date time must be in the future");
            }
        
            // Check if mass is positive
            if (contractPostDTO.getMass() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mass must be positive");
            }
        
            // Check if volume is positive
            if (contractPostDTO.getVolume() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Volume must be positive");
            }
        
            // Check if man power is positive
            if (contractPostDTO.getManPower() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Man power must be positive");
            }
        
            // Check if price is positive
            if (contractPostDTO.getPrice() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be positive");
            }
        
            // Check if collateral is positive
            if (contractPostDTO.getCollateral() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collateral must be positive");
            }
        }
        
    
}
