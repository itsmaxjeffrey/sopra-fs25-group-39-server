package ch.uzh.ifi.hase.soprafs24.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractFilterDTO;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class ContractService {
    
    private final Logger log = LoggerFactory.getLogger(ContractService.class);
    
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public ContractService(@Qualifier("contractRepository") ContractRepository contractRepository,
                          @Qualifier("userRepository") UserRepository userRepository) {
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Creates a new contract entity
     * 
     * @param contract The contract entity to create
     * @return The created contract entity
     */
    public Contract createContract(Contract contract) {
        // Validate requester exists
        final Long requesterId = contract.getRequester().getUserId();
        Requester requester = (Requester) userRepository.findById(requesterId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Requester with ID " + requesterId + " not found"));

        // Set initial contract status
        contract.setContractStatus(ContractStatus.REQUESTED);
        
        // Set requester
        contract.setRequester(requester);
        
        // Save contract to database
        contract = contractRepository.save(contract);
        contractRepository.flush();
        
        log.debug("Created Contract: {}", contract);
        return contract;
    }

    /**
     * Gets all contracts with optional filtering
     * 
     * @param lat Latitude for location-based search (placeholder for future implementation)
     * @param lng Longitude for location-based search (placeholder for future implementation)
     * @param filters Filter criteria
     * @return List of filtered contracts
     */
    public List<Contract> getContracts(Double lat, Double lng, ContractFilterDTO filters) {
        List<Contract> contracts = contractRepository.findAll();
        
        return contracts.stream()
            .filter(contract -> {
                if (filters == null) return true;
                
                // Filter by price
                if (filters.getPrice() != null && contract.getPrice() > filters.getPrice()) {
                    return false;
                }
                
                // Filter by weight (mass)
                if (filters.getWeight() != null && contract.getMass() > filters.getWeight()) {
                    return false;
                }
                
                // Filter by dimensions (assuming volume is calculated from height, length, width)
                if (filters.getHeight() != null && filters.getLength() != null && filters.getWidth() != null) {
                    double maxVolume = filters.getHeight() * filters.getLength() * filters.getWidth();
                    if (contract.getVolume() > maxVolume) {
                        return false;
                    }
                }
                
                // Filter by required people
                if (filters.getRequiredPeople() != null && contract.getManPower() > filters.getRequiredPeople()) {
                    return false;
                }
                
                // Filter by fragile items
                if (filters.getFragile() != null && filters.getFragile() && !contract.isFragile()) {
                    return false;
                }
                
                // Filter by cooling required
                if (filters.getCoolingRequired() != null && filters.getCoolingRequired() && !contract.isCoolingRequired()) {
                    return false;
                }
                
                // Filter by ride along
                if (filters.getRideAlong() != null && filters.getRideAlong() && !contract.isRideAlong()) {
                    return false;
                }
                
                // Filter by move date time
                if (filters.getMoveDateTime() != null && !contract.getMoveDateTime().equals(filters.getMoveDateTime())) {
                    return false;
                }
                
                // Placeholder for location-based filtering
                // TODO: Implement proper distance calculation using Google Maps API
                if (lat != null && lng != null && filters.getRadius() != null) {
                    // For now, return all contracts regardless of distance
                    // This will be replaced with actual distance calculation later
                    return true;
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }

    /**
     * Gets all contracts without filtering
     * 
     * @return List of all contracts
     */
    public List<Contract> getContracts() {
        return contractRepository.findAll();
    }

    /**
     * Gets a contract by ID
     * 
     * @param contractId The ID of the contract to retrieve
     * @return The contract entity
     */
    public Contract getContractById(Long contractId) {
        return contractRepository.findById(contractId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Contract with ID " + contractId + " not found"));
    }

    /**
     * Gets all contracts for a specific Requester, optionally filtered by status
     * 
     * @param requesterId The ID of the Requester
     * @param status Optional status to filter by
     * @return List of contracts for the Requester
     */
    public List<Contract> getContractsByRequesterId(Long requesterId, ContractStatus status) {
        if (status != null) {
            return contractRepository.findByRequester_UserIdAndContractStatus(requesterId, status);
        }
        return contractRepository.findByRequester_UserId(requesterId);
    }

    /**
     * Gets all contracts for a specific Driver, optionally filtered by status
     * 
     * @param driverId The ID of the Driver
     * @param status Optional status to filter by
     * @return List of contracts for the Driver
     */
    public List<Contract> getContractsByDriverId(Long driverId, ContractStatus status) {
        if (status != null) {
            return contractRepository.findByDriver_UserIdAndContractStatus(driverId, status);
        }
        return contractRepository.findByDriver_UserId(driverId);
    }

    /**
     * Gets all contracts with a specific status
     * 
     * @param status The status to filter by
     * @return List of contracts with the specified status
     */
    public List<Contract> getContractsByStatus(ContractStatus status) {
        return contractRepository.findByContractStatus(status);
    }

    /**
     * Get all contracts for a specific user with optional status filtering
     * 
     * @param userId The ID of the user
     * @param status Optional status to filter by
     * @return List of contracts for the user
     */
    public List<Contract> getContractsByUser(Long userId, ContractStatus status) {
        List<Contract> contracts = contractRepository.findByRequester_UserId(userId);
        
        // If status is provided, filter by status
        if (status != null) {
            return contracts.stream()
                .filter(contract -> contract.getContractStatus() == status)
                .collect(Collectors.toList());
        }
        
        return contracts;
    }

    /**
     * Updates an existing contract
     * 
     * @param contractId The ID of the contract to update
     * @param contractUpdates The updated contract data
     * @return The updated contract
     * @throws ResponseStatusException if contract is not found or update is invalid
     */
    public Contract updateContract(Long contractId, Contract contractUpdates) {
        // Get existing contract
        Contract existingContract = getContractById(contractId);
        
        // Validate update
        validateContractUpdate(existingContract, contractUpdates);
        
        // Update fields if provided
        if (contractUpdates.getTitle() != null) {
            existingContract.setTitle(contractUpdates.getTitle());
        }
        if (contractUpdates.getMass() > 0) {
            existingContract.setMass(contractUpdates.getMass());
        }
        if (contractUpdates.getVolume() > 0) {
            existingContract.setVolume(contractUpdates.getVolume());
        }
        existingContract.setFragile(contractUpdates.isFragile());
        existingContract.setCoolingRequired(contractUpdates.isCoolingRequired());
        existingContract.setRideAlong(contractUpdates.isRideAlong());
        if (contractUpdates.getManPower() > 0) {
            existingContract.setManPower(contractUpdates.getManPower());
        }
        if (contractUpdates.getContractDescription() != null) {
            existingContract.setContractDescription(contractUpdates.getContractDescription());
        }
        if (contractUpdates.getPrice() > 0) {
            existingContract.setPrice(contractUpdates.getPrice());
        }
        if (contractUpdates.getCollateral() >= 0) {
            existingContract.setCollateral(contractUpdates.getCollateral());
        }
        if (contractUpdates.getFromAddress() != null) {
            existingContract.setFromAddress(contractUpdates.getFromAddress());
        }
        if (contractUpdates.getToAddress() != null) {
            existingContract.setToAddress(contractUpdates.getToAddress());
        }
        if (contractUpdates.getMoveDateTime() != null) {
            existingContract.setMoveDateTime(contractUpdates.getMoveDateTime());
        }
        if (contractUpdates.getContractStatus() != null) {
            existingContract.setContractStatus(contractUpdates.getContractStatus());
        }
        
        // Save updated contract
        Contract updatedContract = contractRepository.save(existingContract);
        contractRepository.flush();
        
        log.debug("Updated Contract: {}", updatedContract);
        return updatedContract;
    }

    /**
     * Validates that a contract update is allowed
     * 
     * @param existingContract The existing contract
     * @param contractUpdates The proposed updates
     * @throws ResponseStatusException if the update is not allowed
     */
    private void validateContractUpdate(Contract existingContract, Contract contractUpdates) {
        // Cannot update a completed or cancelled contract
        if (existingContract.getContractStatus() == ContractStatus.COMPLETED || 
            existingContract.getContractStatus() == ContractStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Cannot update a completed or canceled contract");
        }

        // Cannot change status to a previous status
        if (contractUpdates.getContractStatus() != null && 
            contractUpdates.getContractStatus().ordinal() < existingContract.getContractStatus().ordinal()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Cannot change contract status to a previous status");
        }

        // Cannot change move date to the past
        if (contractUpdates.getMoveDateTime() != null && 
            contractUpdates.getMoveDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot set move date to the past");
        }
    }

    /**
     * Cancel a contract
     * 
     * @param contractId The ID of the contract to cancel
     * @param reason The reason for cancellation
     * @return The cancelled contract
     * @throws ResponseStatusException if the contract cannot be cancelled
     */
    public Contract cancelContract(Long contractId, String reason) {
        Contract contract = getContractById(contractId);
        
        // Check if contract can be cancelled
        if (contract.getContractStatus() == ContractStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Cannot cancel a completed contract");
        }
        
        if (contract.getContractStatus() == ContractStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Contract is already canceled");
        }
        
        // Check if the move date is within 72 hours
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime moveDateTime = contract.getMoveDateTime();
        long hoursUntilMove = ChronoUnit.HOURS.between(now, moveDateTime);
        
        if (hoursUntilMove < 72) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Cannot cancel contract less than 72 hours before move date");
        }
        
        // Update contract status and reason
        contract.setContractStatus(ContractStatus.CANCELED);
        contract.setCancelReason(reason);
        
        // Save the updated contract
        return contractRepository.save(contract);
    }

    /**
     * Mark a contract as fulfilled
     * 
     * @param contractId The ID of the contract to fulfill
     * @return The fulfilled contract
     * @throws ResponseStatusException if the contract cannot be fulfilled
     */
    public Contract fulfillContract(Long contractId) {
        Contract contract = getContractById(contractId);
        
        // Check if contract can be fulfilled
        if (contract.getContractStatus() != ContractStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Only accepted contracts can be fulfilled");
        }
        
        if (contract.getContractStatus() == ContractStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Contract is already completed");
        }
        
        if (contract.getContractStatus() == ContractStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Cannot fulfill a canceled contract");
        }
        
        // Update contract status
        contract.setContractStatus(ContractStatus.COMPLETED);
        
        // Save the updated contract
        return contractRepository.save(contract);
    }

    /**
     * Delete a contract (soft delete)
     * 
     * @param contractId The ID of the contract to delete
     * @throws ResponseStatusException if the contract cannot be deleted
     */
    public void deleteContract(Long contractId) {
        Contract contract = getContractById(contractId);
        
        // Check if contract can be deleted based on status
        if (contract.getContractStatus() != ContractStatus.REQUESTED && 
            contract.getContractStatus() != ContractStatus.OFFERED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Contract can only be deleted in REQUESTED or OFFERED status");
        }
        
        // Check if contract is already deleted
        if (contract.getContractStatus() == ContractStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Contract is already deleted");
        }
        
        // Check if the move date is within 72 hours
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime moveDateTime = contract.getMoveDateTime();
        long hoursUntilMove = ChronoUnit.HOURS.between(now, moveDateTime);
        
        if (hoursUntilMove < 72) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Cannot delete contract less than 72 hours before move date");
        }
        
        // Soft delete by setting status to DELETED
        contract.setContractStatus(ContractStatus.DELETED);
        
        // Save the updated contract
        contractRepository.save(contract);
        contractRepository.flush();
        
        log.debug("Deleted Contract: {}", contract);
    }
}
