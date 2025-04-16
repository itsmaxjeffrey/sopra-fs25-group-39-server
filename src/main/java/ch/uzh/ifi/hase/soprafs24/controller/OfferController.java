package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

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

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.OfferService;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.service.ContractService;

/**
 * Offer Controller
 * This class is responsible for handling all REST request that are related to
 * the offer management.
 */
@RestController
public class OfferController {

    private final OfferService offerService;
    private final AuthorizationService authorizationService;
    private final ContractService contractService;

    OfferController(OfferService offerService, AuthorizationService authorizationService, ContractService contractService) {
        this.offerService = offerService;
        this.authorizationService = authorizationService;
        this.contractService = contractService;
    }

    /**
     * Get all offers with optional filtering
     * 
     * Example calls:
     * GET /api/v1/offers
     * GET /api/v1/offers?contractId=123
     * GET /api/v1/offers?driverId=456
     * GET /api/v1/offers?status=CREATED
     * GET /api/v1/offers?contractId=123&driverId=456&status=CREATED
     */
    @GetMapping("/api/v1/offers")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> getOffers(
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Long contractId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) OfferStatus status) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Handle driver access
        if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Drivers can only see their own offers
            driverId = userId;
        }
        // Handle requester access
        else if (authenticatedUser.getUserAccountType() == UserAccountType.REQUESTER) {
            // Requesters can only see offers for their contracts
            if (contractId != null) {
                // Validate that the contract belongs to the requester
                Contract contract = contractService.getContractById(contractId);
                if (!contract.getRequester().getUserId().equals(userId)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "You are not authorized to view offers for this contract");
                    response.put("timestamp", System.currentTimeMillis());
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                // If no contractId provided, get all contracts for this requester
                List<Contract> requesterContracts = contractService.getContractsByRequesterId(userId, null);
                List<OfferGetDTO> allOffers = new ArrayList<>();
                for (Contract contract : requesterContracts) {
                    allOffers.addAll(offerService.getOffers(contract.getContractId(), null, status));
                }
                Map<String, Object> response = new HashMap<>();
                response.put("offers", allOffers);
                response.put("timestamp", System.currentTimeMillis());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        // Get filtered offers
        List<OfferGetDTO> offers = offerService.getOffers(contractId, driverId, status);

        // Create response with standard format
        Map<String, Object> response = new HashMap<>();
        response.put("offers", offers);
        response.put("timestamp", System.currentTimeMillis());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get a specific offer by ID
     * 
     * Example call:
     * GET /api/v1/offers/789
     */
    @GetMapping("/api/v1/offers/{offerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> getOffer(
            @PathVariable Long offerId,
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Get the offer
        OfferGetDTO offer = offerService.getOffer(offerId);
        
        // Check if user is authorized to view the offer
        if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Driver can only view their own offers
            if (!offer.getDriver().getUserId().equals(userId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "You are not authorized to view this offer");
                response.put("timestamp", System.currentTimeMillis());
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } else if (authenticatedUser.getUserAccountType() == UserAccountType.REQUESTER) {
            // Requester can only view offers for their contracts
            if (!offer.getContract().getRequesterId().equals(userId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "You are not authorized to view this offer");
                response.put("timestamp", System.currentTimeMillis());
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } else {
            // Invalid user type
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid user account type");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // Create response with standard format
        Map<String, Object> response = new HashMap<>();
        response.put("offer", offer);
        response.put("timestamp", System.currentTimeMillis());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all offers for a specific contract
     * 
     * Example call:
     * GET /api/v1/contracts/123/offers
     */
    @GetMapping("/api/v1/contracts/{contractId}/offers")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<OfferGetDTO> getOffersByContract(@PathVariable Long contractId) {
        return offerService.getOffers(contractId, null, null);
    }

    /**
     * Create a new offer
     * 
     * Example call:
     * POST /api/v1/offers
     * Request body:
     * {
     *   "contractId": 123,
     *   "driverId": 456
     * }
     */
    @PostMapping("/api/v1/offers")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public OfferGetDTO createOffer(@RequestBody OfferPostDTO offerPostDTO) {
        return offerService.createOffer(offerPostDTO);
    }

    /**
     * Delete an offer
     * 
     * Example call:
     * DELETE /api/v1/offers/789
     */
    @DeleteMapping("/api/v1/offers/{offerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOffer(@PathVariable Long offerId) {
        offerService.deleteOffer(offerId);
    }

    /**
     * Update the status of an offer
     * 
     * Example call:
     * PUT /api/v1/offers/789/status
     * Request body:
     * {
     *   "status": "ACCEPTED"
     * }
     */
    @PutMapping("/api/v1/offers/{offerId}/status")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public OfferGetDTO updateOfferStatus(@PathVariable Long offerId, @RequestBody OfferPutDTO offerPutDTO) {
        if (offerPutDTO.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        return offerService.updateOfferStatus(offerId, offerPutDTO.getStatus());
    }

    /**
     * Get all offers for a specific driver with optional status filtering
     * 
     * Example calls:
     * GET /api/v1/users/123/offers
     * GET /api/v1/users/123/offers?status=CREATED
     */
    @GetMapping("/api/v1/users/{driverId}/offers")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Object> getOffersByDriver(
            @PathVariable Long driverId,
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) OfferStatus status) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Check if user is authorized to view these offers
        if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Driver can only view their own offers
            if (!driverId.equals(userId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "You are not authorized to view these offers");
                response.put("timestamp", System.currentTimeMillis());
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } else {
            // Only drivers can view offers
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Only drivers can view offers");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // Get offers from service
        List<OfferGetDTO> offers = offerService.getOffers(null, driverId, status);

        // Create response with standard format
        Map<String, Object> response = new HashMap<>();
        response.put("offers", offers);
        response.put("timestamp", System.currentTimeMillis());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 