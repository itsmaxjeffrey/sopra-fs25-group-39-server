package ch.uzh.ifi.hase.soprafs24.offer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.common.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.common.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.contract.model.Contract;
import ch.uzh.ifi.hase.soprafs24.contract.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.offer.dto.request.OfferPostDTO;
import ch.uzh.ifi.hase.soprafs24.offer.dto.response.OfferGetDTO;
import ch.uzh.ifi.hase.soprafs24.offer.mapper.OfferDTOMapper;
import ch.uzh.ifi.hase.soprafs24.offer.model.Offer;
import ch.uzh.ifi.hase.soprafs24.offer.repository.OfferRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.user.model.Driver;
import ch.uzh.ifi.hase.soprafs24.user.model.User;

/**
 * Offer Service
 * This class is the "worker" and responsible for all functionality related to
 * offers (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class OfferService {

    private final Logger log = LoggerFactory.getLogger(OfferService.class);

    private final OfferRepository offerRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final OfferDTOMapper offerDTOMapper = OfferDTOMapper.INSTANCE;

    @Autowired
    public OfferService(
            @Qualifier("offerRepository") OfferRepository offerRepository,
            @Qualifier("contractRepository") ContractRepository contractRepository,
            @Qualifier("userRepository") UserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all offers with optional filtering
     * 
     * @param contractId Optional filter by contract ID
     * @param driverId Optional filter by driver ID
     * @param status Optional filter by offer status
     * @return List of offers matching the filters
     */
    public List<OfferGetDTO> getOffers(Long contractId, Long driverId, OfferStatus status) {
        List<Offer> offers;
        
        if (contractId != null && driverId != null && status != null) {
            offers = offerRepository.findByContract_ContractIdAndDriver_UserIdAndOfferStatus(contractId, driverId, status);
        } else if (contractId != null && driverId != null) {
            offers = offerRepository.findByContract_ContractIdAndDriver_UserId(contractId, driverId);
        } else if (contractId != null && status != null) {
            offers = offerRepository.findByContract_ContractIdAndOfferStatus(contractId, status);
        } else if (driverId != null && status != null) {
            offers = offerRepository.findByDriver_UserIdAndOfferStatus(driverId, status);
        } else if (contractId != null) {
            offers = offerRepository.findByContract_ContractId(contractId);
        } else if (driverId != null) {
            offers = offerRepository.findByDriver_UserId(driverId);
        } else if (status != null) {
            offers = offerRepository.findByOfferStatus(status);
        } else {
            offers = offerRepository.findAll();
        }

        return offers.stream()
                .map(offerDTOMapper::convertEntityToOfferGetDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific offer by ID
     * 
     * @param offerId The ID of the offer to retrieve
     * @return The offer DTO
     * @throws ResponseStatusException if the offer is not found
     */
    public OfferGetDTO getOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));
        return offerDTOMapper.convertEntityToOfferGetDTO(offer);
    }

    /**
     * Create a new offer
     * 
     * @param offerPostDTO The DTO containing the offer details
     * @return The created offer as DTO
     * @throws ResponseStatusException if contract or driver not found, or if offer already exists
     */
    public OfferGetDTO createOffer(OfferPostDTO offerPostDTO) {
        // Check if contract exists
        Contract contract = contractRepository.findById(offerPostDTO.getContractId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found"));

        // Check if user exists and is a driver
        User user = userRepository.findById(offerPostDTO.getDriverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (!(user instanceof Driver)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a driver");
        }
        
        Driver driver = (Driver) user;

        // Check if offer already exists for this contract and driver
        if (offerRepository.findByContract_ContractIdAndDriver_UserId(
                offerPostDTO.getContractId(), offerPostDTO.getDriverId()).size() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Offer already exists for this contract and driver");
        }

        // Create new offer
        Offer offer = offerDTOMapper.convertOfferPostDTOtoEntity(offerPostDTO);
        offer.setContract(contract);
        offer.setDriver(driver);
        offer.setOfferStatus(OfferStatus.CREATED);

        // Save offer
        offer = offerRepository.save(offer);
        offerRepository.flush();

        log.debug("Created offer: {}", offer);

        return offerDTOMapper.convertEntityToOfferGetDTO(offer);
    }

    /**
     * Delete an offer
     * 
     * @param offerId The ID of the offer to delete
     * @throws ResponseStatusException if offer not found or not authorized
     */
    public void deleteOffer(Long offerId) {
        // Check if offer exists
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));

        // Check if offer can be deleted (only CREATED status can be deleted)
        if (offer.getOfferStatus() != OfferStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Only offers with CREATED status can be deleted");
        }

        // Delete the offer
        offerRepository.delete(offer);
        offerRepository.flush();

        log.debug("Deleted offer: {}", offer);
    }

    /**
     * Updates the status of an offer
     * 
     * @param offerId The ID of the offer to update
     * @param status The new status to set
     * @return The updated offer
     * @throws ResponseStatusException if the offer is not found or the status update is invalid
     */
    public OfferGetDTO updateOfferStatus(Long offerId, OfferStatus status) {
        Offer offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));

        // Validate status transition
        if (offer.getOfferStatus() == OfferStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot update a deleted offer");
        }

        if (offer.getOfferStatus() == OfferStatus.ACCEPTED && status != OfferStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot change status of an accepted offer");
        }

        if (offer.getOfferStatus() == OfferStatus.REJECTED && status != OfferStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot change status of a rejected offer");
        }

        // Update the status directly
        offer.setOfferStatus(status);
        offer = offerRepository.save(offer);
        log.debug("Updated status of offer {} to {}", offerId, status);

        return offerDTOMapper.convertEntityToOfferGetDTO(offer);
    }

    /**
     * Accepts an offer
     * 
     * @param offerId The ID of the offer to accept
     * @return The updated offer DTO
     */
    public OfferGetDTO acceptOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));
        
        Contract contract = offer.getContract();
        
        // Validate contract status
        if (contract.getContractStatus() != ContractStatus.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Offers can only be accepted for REQUESTED contracts");
        }
        
        // Set the accepted offer and update contract status
        contract.setAcceptedOffer(offer);
        contract.setContractStatus(ContractStatus.ACCEPTED);
        contract.setAcceptedDateTime(LocalDateTime.now());
        
        // Update offer status
        offer.setOfferStatus(OfferStatus.ACCEPTED);
        
        // Save changes
        contractRepository.save(contract);
        offer = offerRepository.save(offer);
        
        return offerDTOMapper.convertEntityToOfferGetDTO(offer);
    }

    /**
     * Rejects an offer
     * 
     * @param offerId The ID of the offer to reject
     * @return The updated offer DTO
     */
    public OfferGetDTO rejectOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));
        
        Contract contract = offer.getContract();
        
        // Validate contract status
        if (contract.getContractStatus() != ContractStatus.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Offers can only be rejected for REQUESTED contracts");
        }
        
        // Update offer status
        offer.setOfferStatus(OfferStatus.REJECTED);
        
        // Save changes
        offer = offerRepository.save(offer);
        
        return offerDTOMapper.convertEntityToOfferGetDTO(offer);
    }
} 