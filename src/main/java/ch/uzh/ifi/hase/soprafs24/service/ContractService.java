package ch.uzh.ifi.hase.soprafs24.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.util.stream.Collectors;

import java.util.ArrayList;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.OfferRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractFilterDTO;


import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
import ch.uzh.ifi.hase.soprafs24.entity.Offer;
import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import java.time.LocalDate;


@Service
@Transactional
public class ContractService {
    
    private final Logger log = LoggerFactory.getLogger(ContractService.class);
    
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final GoogleMapsService googleMapsService;
    private final OfferRepository offerRepository;
    
    @Autowired
    public ContractService(@Qualifier("contractRepository") ContractRepository contractRepository,
                          @Qualifier("userRepository") UserRepository userRepository,
                          GoogleMapsService googleMapsService,
                          @Qualifier("offerRepository") OfferRepository offerRepository) {
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
        this.googleMapsService = googleMapsService;
        this.offerRepository = offerRepository;
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

        // Validate contract data
        validateContractData(contract);

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

    private void validateContractData(Contract contract) {
        // Validate price and collateral
        if (contract.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be positive");
        }
        if (contract.getCollateral() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collateral cannot be negative");
        }

        // Validate mass and volume
        if (contract.getMass() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mass must be positive");
        }
        if (contract.getVolume() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Volume must be positive");
        }

        // Validate manpower
        if (contract.getManPower() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manpower must be positive");
        }

        // Validate move date time
        if (contract.getMoveDateTime() == null || contract.getMoveDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Move date time must be in the future");
        }

        // Validate locations
        if (contract.getFromAddress() == null || contract.getToAddress() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both from and to addresses must be provided");
        }
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
                
                // Filter by move date
                if (filters.getMoveDate() != null) {
                    LocalDate contractDate = contract.getMoveDateTime().toLocalDate();
                    if (!contractDate.equals(filters.getMoveDate())) {
                        return false;
                    }
                }
                
                // Location-based filtering using Google Maps API
                if (lat != null && lng != null && filters.getRadius() != null && contract.getFromAddress() != null) {
                    double distance = googleMapsService.calculateDistance(
                        lat, lng,
                        contract.getFromAddress().getLatitude(),
                        contract.getFromAddress().getLongitude()
                    );
                    return distance <= filters.getRadius();
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
        // Validate price and collateral updates
        if (contractUpdates.getPrice() > 0 && contractUpdates.getPrice() != existingContract.getPrice()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be changed after contract creation");
        }
        if (contractUpdates.getCollateral() >= 0 && contractUpdates.getCollateral() != existingContract.getCollateral()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collateral cannot be changed after contract creation");
        }

        // Validate mass and volume updates
        if (contractUpdates.getMass() > 0 && contractUpdates.getMass() != existingContract.getMass()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mass cannot be changed after contract creation");
        }
        if (contractUpdates.getVolume() > 0 && contractUpdates.getVolume() != existingContract.getVolume()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Volume cannot be changed after contract creation");
        }

        // Validate manpower update
        if (contractUpdates.getManPower() > 0 && contractUpdates.getManPower() != existingContract.getManPower()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manpower cannot be changed after contract creation");
        }

        // Validate move date time update
        if (contractUpdates.getMoveDateTime() != null && 
            !contractUpdates.getMoveDateTime().equals(existingContract.getMoveDateTime())) {
            if (contractUpdates.getMoveDateTime().isBefore(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Move date time must be in the future");
            }
            if (existingContract.getContractStatus() != ContractStatus.REQUESTED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Move date time can only be changed for REQUESTED contracts");
            }
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
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Contract cancelContract(Long contractId, String reason) {
        // Get contract with optimistic locking
        Contract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Contract with ID " + contractId + " not found"));
        
        // Check if contract can be canceled
        if (contract.getContractStatus() == ContractStatus.COMPLETED || 
            contract.getContractStatus() == ContractStatus.FINALIZED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Cannot cancel a completed or finalized contract");
        }
        
        if (contract.getContractStatus() == ContractStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Contract is already canceled");
        }
        
        // Check if the move date is within 72 hours for ACCEPTED contracts
        if (contract.getContractStatus() == ContractStatus.ACCEPTED) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime moveDateTime = contract.getMoveDateTime();
            long hoursUntilMove = ChronoUnit.HOURS.between(now, moveDateTime);
            
            if (hoursUntilMove < 72) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Cannot cancel an accepted contract less than 72 hours before move date");
            }
        }
        
        try {
            // Reject all offers for this contract in a single operation
            List<Offer> offers = offerRepository.findByContract_ContractId(contractId);
            for (Offer offer : offers) {
                if (offer.getOfferStatus() != OfferStatus.REJECTED) {
                    offer.setOfferStatus(OfferStatus.REJECTED);
                }
            }
            offerRepository.saveAll(offers);
            
            // Update contract status and reason
            contract.setContractStatus(ContractStatus.CANCELED);
            contract.setCancelReason(reason);
            
            // Save the updated contract
            Contract savedContract = contractRepository.save(contract);
            
            // Flush changes to ensure they're persisted
            contractRepository.flush();
            offerRepository.flush();
            
            return savedContract;
        } catch (Exception e) {
            // Log the error
            log.error("Error during contract cancellation: {}", e.getMessage());
            // The transaction will be automatically rolled back
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error during contract cancellation. All changes have been rolled back.");
        }
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
        if (contract.getContractStatus() == ContractStatus.COMPLETED || 
            contract.getContractStatus() == ContractStatus.FINALIZED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Cannot delete a completed or finalized contract");
        }
        
        // Check if contract is already deleted
        if (contract.getContractStatus() == ContractStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Contract is already deleted");
        }
        
        // Check if the move date is within 72 hours for ACCEPTED contracts
        if (contract.getContractStatus() == ContractStatus.ACCEPTED) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime moveDateTime = contract.getMoveDateTime();
            long hoursUntilMove = ChronoUnit.HOURS.between(now, moveDateTime);
            
            if (hoursUntilMove < 72) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Cannot delete an accepted contract less than 72 hours before move date");
            }
        }
        
        // Reject all offers for this contract
        List<Offer> offers = offerRepository.findByContract_ContractId(contractId);
        for (Offer offer : offers) {
            if (offer.getOfferStatus() != OfferStatus.REJECTED) {
                offer.setOfferStatus(OfferStatus.REJECTED);
                offerRepository.save(offer);
            }
        }
        
        // If contract was in OFFERED state and this was the last offer, revert to REQUESTED
        if (contract.getContractStatus() == ContractStatus.OFFERED && 
            offers.stream().allMatch(o -> o.getOfferStatus() == OfferStatus.REJECTED)) {
            contract.setContractStatus(ContractStatus.REQUESTED);
        } else {
            // Otherwise, mark as deleted
            contract.setContractStatus(ContractStatus.DELETED);
        }
        
        // Save the updated contract
        contractRepository.save(contract);
        contractRepository.flush();
        
        log.debug("Deleted Contract: {}", contract);
    }

    /**
     * Automatically update contract statuses based on move date
     * This method should be called periodically to update contracts
     */
    @Scheduled(fixedRate = 21600000) // Run every 6 hours
    public void updateContractStatuses() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find all ACCEPTED contracts where move date has passed
        List<Contract> contractsToComplete = contractRepository.findByContractStatusAndMoveDateTimeBefore(
            ContractStatus.ACCEPTED, now);
            
        for (Contract contract : contractsToComplete) {
            contract.setContractStatus(ContractStatus.COMPLETED);
            log.debug("Automatically updated contract {} to COMPLETED status", contract.getContractId());
        }
        
        // Find all REQUESTED or OFFERED contracts where move date has passed
        List<Contract> contractsToCancel = new ArrayList<>();
        contractsToCancel.addAll(contractRepository.findByContractStatusAndMoveDateTimeBefore(
            ContractStatus.REQUESTED, now));
        contractsToCancel.addAll(contractRepository.findByContractStatusAndMoveDateTimeBefore(
            ContractStatus.OFFERED, now));
            
        for (Contract contract : contractsToCancel) {
            contract.setContractStatus(ContractStatus.CANCELED);
            contract.setCancelReason("Contract automatically canceled due to expired move date");
            
            // Reject all offers for this contract
            List<Offer> offers = offerRepository.findByContract_ContractId(contract.getContractId());
            for (Offer offer : offers) {
                if (offer.getOfferStatus() != OfferStatus.REJECTED) {
                    offer.setOfferStatus(OfferStatus.REJECTED);
                    offerRepository.save(offer);
                }
            }
            
            log.debug("Automatically canceled contract {} due to expired move date", contract.getContractId());
        }
        
        // Save all changes
        if (!contractsToComplete.isEmpty() || !contractsToCancel.isEmpty()) {
            contractRepository.saveAll(contractsToComplete);
            contractRepository.saveAll(contractsToCancel);
            contractRepository.flush();
        }
    }

    public Contract completeContract(Long contractId) {
        Contract contract = getContractById(contractId);
        
        // Validate contract can be completed
        if (contract.getContractStatus() != ContractStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Only accepted contracts can be completed");
        }
        
        // Check if move date has passed
        LocalDateTime now = LocalDateTime.now();
        if (contract.getMoveDateTime().isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Cannot complete contract before move date");
        }
        
        // Update contract status
        contract.setContractStatus(ContractStatus.COMPLETED);
        
        // Save the updated contract
        return contractRepository.save(contract);
    }

    public Contract finalizeContract(Long contractId) {
        Contract contract = getContractById(contractId);
        
        // Validate contract can be finalized
        if (contract.getContractStatus() != ContractStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Only completed contracts can be finalized");
        }
        
        // Update contract status
        contract.setContractStatus(ContractStatus.FINALIZED);
        
        // Save the updated contract
        return contractRepository.save(contract);
    }

    /**
     * Handles cleanup when a driver is deleted
     * - Rejects all CREATED offers from the driver
     * - Updates contract status if needed
     * 
     * @param driverId The ID of the driver being deleted
     */
    public void handleDriverDeletion(Long driverId) {
        // Get all offers from the driver
        List<Offer> offers = offerRepository.findByDriver_UserId(driverId);
        
        for (Offer offer : offers) {
            Contract contract = offer.getContract();
            
            // Only handle CREATED offers
            if (offer.getOfferStatus() == OfferStatus.CREATED) {
                // Update offer status
                offer.setOfferStatus(OfferStatus.REJECTED);
                offerRepository.save(offer);
                
                // If this was the last offer and contract is in OFFERED state, revert to REQUESTED
                List<Offer> remainingOffers = offerRepository.findByContract_ContractIdAndOfferStatus(
                    contract.getContractId(), OfferStatus.CREATED);
                if (contract.getContractStatus() == ContractStatus.OFFERED && remainingOffers.isEmpty()) {
                    contract.setContractStatus(ContractStatus.REQUESTED);
                    contractRepository.save(contract);
                }
            }
        }
        
        // Flush changes
        offerRepository.flush();
        contractRepository.flush();
        
        log.debug("Handled driver deletion for driverId: {}", driverId);
    }

    /**
     * Handles cleanup when a requester is deleted
     * - Deletes all REQUESTED and OFFERED contracts
     * - Rejects all offers for these contracts
     * 
     * @param requesterId The ID of the requester being deleted
     */
    public void handleRequesterDeletion(Long requesterId) {
        // Get all contracts from the requester
        List<Contract> contracts = contractRepository.findByRequester_UserId(requesterId);
        
        for (Contract contract : contracts) {
            // Only handle REQUESTED and OFFERED contracts
            if (contract.getContractStatus() == ContractStatus.REQUESTED || 
                contract.getContractStatus() == ContractStatus.OFFERED) {
                
                // Reject all offers for this contract
                List<Offer> offers = offerRepository.findByContract_ContractId(contract.getContractId());
                for (Offer offer : offers) {
                    if (offer.getOfferStatus() != OfferStatus.REJECTED) {
                        offer.setOfferStatus(OfferStatus.REJECTED);
                        offerRepository.save(offer);
                    }
                }
                
                // Delete the contract
                contract.setContractStatus(ContractStatus.DELETED);
                contractRepository.save(contract);
            }
        }
        
        // Flush changes
        offerRepository.flush();
        contractRepository.flush();
        
        log.debug("Handled requester deletion for requesterId: {}", requesterId);
    }
}
