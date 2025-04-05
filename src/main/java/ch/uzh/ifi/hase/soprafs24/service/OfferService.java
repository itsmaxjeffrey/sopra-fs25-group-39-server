package ch.uzh.ifi.hase.soprafs24.service;

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

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Offer;
import ch.uzh.ifi.hase.soprafs24.repository.OfferRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.OfferGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.OfferDTOMapper;

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

    @Autowired
    public OfferService(@Qualifier("offerRepository") OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
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
                .map(OfferDTOMapper.INSTANCE::convertEntityToOfferGetDTO)
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
        return OfferDTOMapper.INSTANCE.convertEntityToOfferGetDTO(offer);
    }

    public Offer createOffer(Long contractId, Long driverId) {
        // Implementation will be added later
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void deleteOffer(Long offerId) {
        // Implementation will be added later
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public OfferGetDTO updateOfferStatus(Long offerId, OfferStatus status) {
        // Implementation will be added later
        throw new UnsupportedOperationException("Not implemented yet");
    }
} 