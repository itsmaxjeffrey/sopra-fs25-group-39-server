package ch.uzh.ifi.hase.soprafs24.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractCancelDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractFilterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.ContractDTOMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.LocationDTOMapper;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.service.ContractPollingService;
import ch.uzh.ifi.hase.soprafs24.service.ContractService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.UserDTOMapper;

@RestController
public class ContractController {

    // Error message constants
    private static final String ERROR_INVALID_CREDENTIALS = "Invalid credentials";
    private static final String ERROR_ONLY_REQUESTERS_CAN_CREATE = "Only requesters can create contracts";
    private static final String ERROR_ONLY_REQUESTERS_CAN_UPDATE = "Only requesters can update contracts";
    private static final String ERROR_ONLY_REQUESTERS_CAN_CANCEL = "Only requesters can cancel contracts";
    private static final String ERROR_NOT_AUTHORIZED_TO_VIEW = "You are not authorized to view these contracts";
    private static final String ERROR_NOT_AUTHORIZED_TO_VIEW_CONTRACT = "You are not authorized to view this contract";
    private static final String ERROR_NOT_AUTHORIZED_TO_UPDATE = "You are not authorized to update this contract";
    private static final String ERROR_NOT_AUTHORIZED_TO_CANCEL = "You are not authorized to cancel this contract";
    private static final String ERROR_NOT_AUTHORIZED_TO_DELETE = "You are not authorized to delete this contract";
    private static final String ERROR_NOT_AUTHORIZED_TO_FULFILL = "You are not authorized to fulfill this contract";
    private static final String ERROR_NOT_AUTHORIZED_TO_VIEW_DRIVER = "You are not authorized to view driver details for this contract";

    private final UserRepository userRepository;
    private final ContractService contractService;
    private final LocationService locationService;
    private final ContractPollingService contractPollingService;
    private final AuthorizationService authorizationService;
    private final UserDTOMapper userDTOMapper;

    public ContractController(
            ContractService contractService, 
            LocationService locationService, 
            ContractPollingService contractPollingService, 
            UserRepository userRepository,
            AuthorizationService authorizationService,
            UserDTOMapper userDTOMapper) {
        this.contractService = contractService;
        this.locationService = locationService;
        this.contractPollingService = contractPollingService;
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
        this.userDTOMapper = userDTOMapper;
    }

    /**
     * Helper method to create a standardized response
     * @param data The main data to include in the response
     * @param message Optional message to include
     * @param status The HTTP status code
     * @param customKey Optional custom key to use instead of contract/contracts
     * @return ResponseEntity with standardized format
     */
    private ResponseEntity<Object> createResponse(Object data, String message, HttpStatus status, String customKey) {
        Map<String, Object> response = new HashMap<>();
        if (data != null) {
            if (customKey != null) {
                response.put(customKey, data);
            } else if (data instanceof List) {
                response.put("contracts", data);
            } else {
                response.put("contract", data);
            }
        }
        if (message != null) {
            response.put("message", message);
        }
        response.put("timestamp", System.currentTimeMillis());
        return new ResponseEntity<>(response, status);
    }

    private ResponseEntity<Object> createResponse(Object data, String message, HttpStatus status) {
        return createResponse(data, message, status, null);
    }

    /**
     * Get all contracts with optional filtering
     * 
     * Example request with filters:
     * GET /api/v1/contracts?lat=47.3769&lng=8.5417&filters={"radius": 10, "price": 100, "weight": 50, "height": 2, "length": 3, "width": 1.5, "requiredPeople": 2, "fragile": true, "coolingRequired": false, "rideAlong": true, "fromAddress": "Zurich", "toAddress": "Bern", "moveDate": "2024-04-15"}
     * 
     * @param userId User ID from header
     * @param token Authentication token from header
     * @param lat Latitude for location-based search
     * @param lng Longitude for location-based search
     * @param filters JSON string containing filter criteria
     * @return List of contracts matching the criteria
     */
    @GetMapping("/api/v1/contracts")
    public ResponseEntity<Object> getAllContracts(
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) String filters) {

        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            return createResponse(null, ERROR_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }

        // Parse filters if provided
        ContractFilterDTO filterDTO = null;
        if (filters != null && !filters.isEmpty()) {
            try {
                filterDTO = new ObjectMapper().readValue(filters, ContractFilterDTO.class);
                
                // Validate moveDate format if provided
                if (filterDTO.getMoveDate() != null) {
                    try {
                        LocalDate.parse(filterDTO.getMoveDate().toString());
                    } catch (Exception e) {
                        return createResponse(null, "Invalid moveDate format. Expected format: yyyy-MM-dd", HttpStatus.BAD_REQUEST);
                    }
                }
            } catch (Exception e) {
                return createResponse(null, "Invalid filters format", HttpStatus.BAD_REQUEST);
            }
        }

        // Get filtered contracts from service
        List<Contract> contracts = contractService.getContracts(lat, lng, filterDTO);
        
        // Convert to DTOs
        List<ContractGetDTO> contractDTOs = contracts.stream()
                .map(ContractDTOMapper.INSTANCE::convertContractEntityToContractGetDTO)
                .collect(Collectors.toList());

        return createResponse(contractDTOs, null, HttpStatus.OK);
    }

    /**
     * Create a new contract
     * @param userId User ID from header
     * @param token Authentication token from header
     * @param contractPostDTO the contract data
     * @return the created contract
     */
    @PostMapping("/api/v1/contracts")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<Object> createContract(
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token,
            @RequestBody ContractPostDTO contractPostDTO) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            return createResponse(null, ERROR_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }

        // Verify user is a requester
        if (!authenticatedUser.getUserAccountType().equals(UserAccountType.REQUESTER)) {
            return createResponse(null, ERROR_ONLY_REQUESTERS_CAN_CREATE, HttpStatus.FORBIDDEN);
        }

        validateContractPostDTO(contractPostDTO);

        // Create locations first
        Location fromLocation = LocationDTOMapper.INSTANCE.convertLocationDTOToEntity(contractPostDTO.getFromLocation());
        Location toLocation = LocationDTOMapper.INSTANCE.convertLocationDTOToEntity(contractPostDTO.getToLocation());

        fromLocation = locationService.createLocation(fromLocation);
        toLocation = locationService.createLocation(toLocation);

        // Convert DTO to entity
        Contract contractInput = ContractDTOMapper.INSTANCE.convertContractPostDTOtoEntity(contractPostDTO);
        contractInput.setFromAddress(fromLocation);
        contractInput.setToAddress(toLocation);
        contractInput.setRequester(ContractDTOMapper.INSTANCE.map(userId)); // Set the requester using the mapper

        // Initialize contract photos if not provided
        if (contractInput.getContractPhotos() == null) {
            contractInput.setContractPhotos(new ArrayList<>());
        }

        // Create contract
        Contract createdContract = contractService.createContract(contractInput);

        // Notify waiting clients via ContractPollingService
        contractPollingService.updateFutures(createdContract, fromLocation.getLatitude(), fromLocation.getLongitude());

        return createResponse(ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(createdContract), null, HttpStatus.CREATED);
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
            if (contractPostDTO.getFromLocation().getLatitude().equals(contractPostDTO.getToLocation().getLatitude()) && 
                contractPostDTO.getFromLocation().getLongitude().equals(contractPostDTO.getToLocation().getLongitude())) {
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

    /**
     * Get a specific contract by ID
     * 
     * Example request:
     * GET /api/v1/contracts/123
     * 
     * @param contractId The ID of the contract to retrieve
     * @return The contract details
     */
    @GetMapping("/api/v1/contracts/{contractId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> getContractById(
            @PathVariable Long contractId,
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            return createResponse(null, ERROR_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }

        // Get contract from service
        Contract contract = contractService.getContractById(contractId);
        
        // Check if user is authorized to view the contract
        if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Drivers can only access contracts that are:
            // 1. In REQUESTED state (available for offers)
            // 2. In OFFERED state (available for offers)
            // 3. Assigned to them (ACCEPTED state)
            if (contract.getContractStatus() == ContractStatus.REQUESTED || 
                contract.getContractStatus() == ContractStatus.OFFERED) {
                // All drivers can see REQUESTED and OFFERED contracts
                return createResponse(ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(contract), null, HttpStatus.OK);
            } else if (contract.getContractStatus() == ContractStatus.ACCEPTED) {
                // Only the assigned driver can see ACCEPTED contracts
                if (contract.getDriver() != null && contract.getDriver().getUserId().equals(userId)) {
                    return createResponse(ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(contract), null, HttpStatus.OK);
                }
            }
            
            // If none of the above conditions are met, the driver is not authorized
            return createResponse(null, ERROR_NOT_AUTHORIZED_TO_VIEW_CONTRACT, HttpStatus.FORBIDDEN);
        } else if (authenticatedUser.getUserAccountType() == UserAccountType.REQUESTER) {
            // Requesters can only access their own contracts
            if (!contract.getRequester().getUserId().equals(userId)) {
                return createResponse(null, ERROR_NOT_AUTHORIZED_TO_VIEW_CONTRACT, HttpStatus.FORBIDDEN);
            }
            return createResponse(ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(contract), null, HttpStatus.OK);
        }
        
        // This should never happen, but just in case
        return createResponse(null, "Invalid user account type", HttpStatus.FORBIDDEN);
    }

    /**
     * Update a specific contract
     * 
     * Example request:
     * PUT /api/v1/contracts/123
     * {
     *   "title": "Updated Title",
     *   "price": 150.0,
     *   "moveDateTime": "2024-05-01T10:00:00"
     * }
     * 
     * @param contractId The ID of the contract to update
     * @param contractPutDTO The updated contract data
     * @return The updated contract details
     */
    @PutMapping("/api/v1/contracts/{contractId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> updateContract(
            @PathVariable Long contractId,
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token,
            @RequestBody ContractPutDTO contractPutDTO) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            return createResponse(null, ERROR_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }

        // Verify user is a requester
        if (!authenticatedUser.getUserAccountType().equals(UserAccountType.REQUESTER)) {
            return createResponse(null, ERROR_ONLY_REQUESTERS_CAN_UPDATE, HttpStatus.FORBIDDEN);
        }

        // Get contract from service
        Contract contract = contractService.getContractById(contractId);
        
        // Check if user is authorized to update the contract
        if (!contract.getRequester().getUserId().equals(userId)) {
            return createResponse(null, ERROR_NOT_AUTHORIZED_TO_UPDATE, HttpStatus.FORBIDDEN);
        }

        // Validate required fields
        validateContractPutDTO(contractPutDTO);

        // Convert DTO to entity
        Contract contractUpdates = ContractDTOMapper.INSTANCE.convertContractPutDTOtoEntity(contractPutDTO);

        // Update contract
        Contract updatedContract = contractService.updateContract(contractId, contractUpdates);

        return createResponse(ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(updatedContract), null, HttpStatus.OK);
    }

    /**
     * Validate the required fields in contract put DTO
     */
    private void validateContractPutDTO(ContractPutDTO contractPutDTO) {
        // Only validate fields that are provided (not null)
        
        // Check if title is provided and not empty
        if (contractPutDTO.getTitle() != null && contractPutDTO.getTitle().trim().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot be empty");
        }

        // Check if move date time is in the future
        if (contractPutDTO.getMoveDateTime() != null && contractPutDTO.getMoveDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Move date time must be in the future");
        }

        // Check if mass is positive
        if (contractPutDTO.getMass() != null && contractPutDTO.getMass() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mass must be positive");
        }

        // Check if volume is positive
        if (contractPutDTO.getVolume() != null && contractPutDTO.getVolume() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Volume must be positive");
        }

        // Check if man power is positive
        if (contractPutDTO.getManPower() != null && contractPutDTO.getManPower() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Man power must be positive");
        }

        // Check if price is positive
        if (contractPutDTO.getPrice() != null && contractPutDTO.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be positive");
        }

        // Check if collateral is positive
        if (contractPutDTO.getCollateral() != null && contractPutDTO.getCollateral() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collateral must be positive");
        }

        // Check if from location is valid when provided
        if (contractPutDTO.getFromLocation() != null) {
            if (contractPutDTO.getFromLocation().getLatitude() == null || 
                contractPutDTO.getFromLocation().getLongitude() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From location must have a valid latitude and longitude");
            }
        }

        // Check if to location is valid when provided
        if (contractPutDTO.getToLocation() != null) {
            if (contractPutDTO.getToLocation().getLatitude() == null || 
                contractPutDTO.getToLocation().getLongitude() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "To location must have a valid latitude and longitude");
            }
        }

        // Check if both locations are provided and are the same
        if (contractPutDTO.getFromLocation() != null && contractPutDTO.getToLocation() != null) {
            if (contractPutDTO.getFromLocation().getLatitude().equals(contractPutDTO.getToLocation().getLatitude()) && 
                contractPutDTO.getFromLocation().getLongitude().equals(contractPutDTO.getToLocation().getLongitude())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From and to locations cannot be the same");
            }
        }
    }

    /**
     * Cancel a contract
     * 
     * @param contractId The ID of the contract to cancel
     * @param contractCancelDTO The cancellation request containing the reason
     * @return The cancelled contract
     */
    @PutMapping("/api/v1/contracts/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> cancelContract(
            @PathVariable("id") Long contractId,
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token,
            @RequestBody ContractCancelDTO contractCancelDTO) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            return createResponse(null, ERROR_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }

        // Verify user is a requester
        if (!authenticatedUser.getUserAccountType().equals(UserAccountType.REQUESTER)) {
            return createResponse(null, ERROR_ONLY_REQUESTERS_CAN_CANCEL, HttpStatus.FORBIDDEN);
        }

        // Get contract from service
        Contract contract = contractService.getContractById(contractId);
        
        // Check if user is authorized to cancel the contract
        if (!contract.getRequester().getUserId().equals(userId)) {
            return createResponse(null, ERROR_NOT_AUTHORIZED_TO_CANCEL, HttpStatus.FORBIDDEN);
        }

        // Check contract status - only ACCEPTED contracts can be cancelled
        if (contract.getContractStatus() != ContractStatus.ACCEPTED) {
            return createResponse(null, "Only ACCEPTED contracts can be cancelled. Use delete for REQUESTED or OFFERED contracts.", HttpStatus.BAD_REQUEST);
        }
        
        // Validate cancellation reason
        if (contractCancelDTO.getReason() == null || contractCancelDTO.getReason().trim().isEmpty()) {
            return createResponse(null, "Cancellation reason is required", HttpStatus.BAD_REQUEST);
        }
        
        // Cancel the contract
        Contract cancelledContract = contractService.cancelContract(contractId, contractCancelDTO.getReason());
        
        return createResponse(ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(cancelledContract), null, HttpStatus.OK);
    }

    /**
     * Mark a contract as fulfilled
     * 
     * @param contractId The ID of the contract to fulfill
     * @return The fulfilled contract
     */
    @PutMapping("/api/v1/contracts/{id}/fulfill")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> fulfillContract(
            @PathVariable("id") Long contractId,
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            return createResponse(null, ERROR_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }

        // Get contract from service
        Contract contract = contractService.getContractById(contractId);
        
        // Check if user is authorized to fulfill the contract
        if (authenticatedUser.getUserAccountType() == UserAccountType.REQUESTER) {
            // Only the requester who created the contract can fulfill it
            if (!contract.getRequester().getUserId().equals(userId)) {
                return createResponse(null, ERROR_NOT_AUTHORIZED_TO_FULFILL, HttpStatus.FORBIDDEN);
            }
        } else if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Only the driver assigned to the contract can fulfill it
            if (contract.getDriver() == null || !contract.getDriver().getUserId().equals(userId)) {
                return createResponse(null, ERROR_NOT_AUTHORIZED_TO_FULFILL, HttpStatus.FORBIDDEN);
            }
        } else {
            // Invalid user type
            return createResponse(null, "Invalid user account type", HttpStatus.FORBIDDEN);
        }

        // Fulfill the contract
        Contract fulfilledContract = contractService.fulfillContract(contractId);
        
        return createResponse(ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(fulfilledContract), null, HttpStatus.OK);
    }

    /**
     * Get all contracts for a specific user with optional status filtering
     * 
     * Example request:
     * GET /api/v1/users/123/contracts?status=REQUESTED
     * 
     * @param userId The ID of the user
     * @param status Optional status to filter by
     * @return List of contracts for the user
     */
    @GetMapping("/api/v1/users/{userId}/contracts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> getUserContracts(
            @PathVariable Long userId,
            @RequestHeader("UserId") Long requestUserId,
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) ContractStatus status) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(requestUserId, token);
        if (authenticatedUser == null) {
            return createResponse(null, ERROR_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }

        // Check if user is authorized to view these contracts
        if (!authenticatedUser.getUserId().equals(userId)) {
            return createResponse(null, ERROR_NOT_AUTHORIZED_TO_VIEW, HttpStatus.FORBIDDEN);
        }
        
        // Check if user is a Requester
        List<Contract> contracts;
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "User with ID " + userId + " not found"));
            
        if (user instanceof Requester) {
            // Get contracts from service with optional status filter
            contracts = contractService.getContractsByRequesterId(userId, status);
        } else {
            // Get contracts from service with optional status filter
            contracts = contractService.getContractsByDriverId(userId, status);
        }
        
        // Convert to DTOs
        List<ContractGetDTO> contractDTOs = contracts.stream()
            .map(ContractDTOMapper.INSTANCE::convertContractEntityToContractGetDTO)
            .collect(Collectors.toList());

        return createResponse(contractDTOs, null, HttpStatus.OK);
    }

    /**
     * Delete a contract
     * 
     * @param contractId The ID of the contract to delete
     * @param userId User ID from header
     * @param token Authentication token from header
     */
    @DeleteMapping("/api/v1/contracts/{contractId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContract(
            @PathVariable Long contractId,
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ERROR_INVALID_CREDENTIALS);
        }

        // Get contract from service
        Contract contract = contractService.getContractById(contractId);
        
        // Check if user is authorized to delete the contract
        if (authenticatedUser.getUserAccountType() == UserAccountType.REQUESTER) {
            // Only the requester who created the contract can delete it
            if (!contract.getRequester().getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, ERROR_NOT_AUTHORIZED_TO_DELETE);
            }
        } else {
            // Drivers cannot delete contracts
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only requesters can delete contracts");
        }

        // If authorized, delete the contract
        contractService.deleteContract(contractId);
    }

    /**
     * Get driver details for a specific contract
     * 
     * Example request:
     * GET /api/v1/contracts/123/driver
     * 
     * @param contractId The ID of the contract
     * @param userId User ID from header
     * @param token Authentication token from header
     * @return The driver details if authorized and driver is assigned
     */
    @GetMapping("/api/v1/contracts/{contractId}/driver")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> getContractDriver(
            @PathVariable Long contractId,
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            return createResponse(null, ERROR_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }

        // Get contract from service
        Contract contract;
        try {
            contract = contractService.getContractById(contractId);
        } catch (ResponseStatusException e) {
            return createResponse(null, "Contract not found", HttpStatus.NOT_FOUND);
        }
        
        // Check if user is authorized to view the driver details
        if (authenticatedUser.getUserAccountType() == UserAccountType.REQUESTER) {
            // Requesters can only view driver details for their own contracts
            if (!contract.getRequester().getUserId().equals(userId)) {
                return createResponse(null, ERROR_NOT_AUTHORIZED_TO_VIEW_DRIVER, HttpStatus.FORBIDDEN);
            }
        } else if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Drivers can only view their own details
            if (contract.getDriver() == null || !contract.getDriver().getUserId().equals(userId)) {
                return createResponse(null, ERROR_NOT_AUTHORIZED_TO_VIEW_DRIVER, HttpStatus.FORBIDDEN);
            }
        } else {
            // Invalid user type
            return createResponse(null, "Invalid user account type", HttpStatus.FORBIDDEN);
        }

        // Check if driver is assigned
        if (contract.getDriver() == null) {
            return createResponse(null, "No driver assigned to this contract", HttpStatus.NOT_FOUND);
        }

        return createResponse(userDTOMapper.convertToDTO(contract.getDriver()), null, HttpStatus.OK, "driver");
    }
}
