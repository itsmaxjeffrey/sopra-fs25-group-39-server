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

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
}
