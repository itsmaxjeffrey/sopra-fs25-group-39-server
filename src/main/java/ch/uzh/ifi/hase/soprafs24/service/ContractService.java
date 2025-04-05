package ch.uzh.ifi.hase.soprafs24.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

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
     * @param status Filter by contract status
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @param minDate Minimum move date
     * @param maxDate Maximum move date
     * @return List of filtered contracts
     */
    public List<Contract> getContracts(ContractStatus status, Double minPrice, Double maxPrice, 
                                     LocalDateTime minDate, LocalDateTime maxDate) {
        List<Contract> contracts = contractRepository.findAll();
        
        return contracts.stream()
            .filter(contract -> status == null || contract.getContractStatus() == status)
            .filter(contract -> minPrice == null || contract.getPrice() >= minPrice)
            .filter(contract -> maxPrice == null || contract.getPrice() <= maxPrice)
            .filter(contract -> minDate == null || !contract.getMoveDateTime().isBefore(minDate))
            .filter(contract -> maxDate == null || !contract.getMoveDateTime().isAfter(maxDate))
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
     * Gets all contracts for a specific user
     * 
     * @param userId The ID of the user
     * @return List of contracts for the user
     */
    public List<Contract> getContractsByUser(Long userId) {
        return contractRepository.findByRequester_UserId(userId);
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
}
