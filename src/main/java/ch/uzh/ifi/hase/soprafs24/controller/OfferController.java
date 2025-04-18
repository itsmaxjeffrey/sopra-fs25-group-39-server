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
import ch.uzh.ifi.hase.soprafs24.service.OfferService;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.service.ContractService;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;

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
     * Helper method to create a standardized response
     * @param data The main data to include in the response
     * @param message Optional message to include
     * @param status The HTTP status code
     * @return ResponseEntity with standardized format
     */
    private ResponseEntity<Object> createResponse(Object data, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        if (data != null) {
            if (data instanceof List) {
                response.put("offers", data);
            } else {
                response.put("offer", data);
            }
        }
        if (message != null) {
            response.put("message", message);
        }
        response.put("timestamp", System.currentTimeMillis());
        return new ResponseEntity<>(response, status);
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
            return createResponse(null, "Invalid credentials", HttpStatus.UNAUTHORIZED);
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
                    return createResponse(null, "You are not authorized to view offers for this contract", HttpStatus.FORBIDDEN);
                }
            } else {
                // If no contractId provided, get all contracts for this requester
                List<Contract> requesterContracts = contractService.getContractsByRequesterId(userId, null);
                List<OfferGetDTO> allOffers = new ArrayList<>();
                for (Contract contract : requesterContracts) {
                    allOffers.addAll(offerService.getOffers(contract.getContractId(), null, status));
                }
                return createResponse(allOffers, null, HttpStatus.OK);
            }
        }

        // Get filtered offers
        List<OfferGetDTO> offers = offerService.getOffers(contractId, driverId, status);
        return createResponse(offers, null, HttpStatus.OK);
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
            return createResponse(null, "Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        // Get the offer
        OfferGetDTO offer = offerService.getOffer(offerId);
        
        // Check if user is authorized to view the offer
        if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Driver can only view their own offers
            if (!offer.getDriver().getUserId().equals(userId)) {
                return createResponse(null, "You are not authorized to view this offer", HttpStatus.FORBIDDEN);
            }
        } else if (authenticatedUser.getUserAccountType() == UserAccountType.REQUESTER) {
            // Requester can only view offers for their contracts
            if (!offer.getContract().getRequesterId().equals(userId)) {
                return createResponse(null, "You are not authorized to view this offer", HttpStatus.FORBIDDEN);
            }
        } else {
            // Invalid user type
            return createResponse(null, "Invalid user account type", HttpStatus.FORBIDDEN);
        }

        return createResponse(offer, null, HttpStatus.OK);
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
    public ResponseEntity<Object> getOffersByContract(
            @PathVariable Long contractId,
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            return createResponse(null, "Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        // Check if user is authorized to view offers for this contract
        Contract contract = contractService.getContractById(contractId);
        if (contract == null) {
            return createResponse(null, "Contract not found", HttpStatus.NOT_FOUND);
        }

        if (authenticatedUser.getUserAccountType() == UserAccountType.REQUESTER) {
            // Requesters can only view offers for their contracts
            if (!contract.getRequester().getUserId().equals(userId)) {
                return createResponse(null, "You are not authorized to view offers for this contract", HttpStatus.FORBIDDEN);
            }
        } else if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Drivers can view offers for any contract in REQUESTED or OFFERED state
            if (contract.getContractStatus() != ContractStatus.REQUESTED && 
                contract.getContractStatus() != ContractStatus.OFFERED) {
                return createResponse(null, "You are not authorized to view offers for this contract", HttpStatus.FORBIDDEN);
            }
        } else {
            // Invalid user type
            return createResponse(null, "Invalid user account type", HttpStatus.FORBIDDEN);
        }

        // Get offers for the contract
        List<OfferGetDTO> offers = offerService.getOffers(contractId, null, null);
        return createResponse(offers, "Offers retrieved successfully", HttpStatus.OK);
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
    public ResponseEntity<Object> createOffer(
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token,
            @RequestBody OfferPostDTO offerPostDTO) {
        
        // Authenticate user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Verify user is a driver
        if (authenticatedUser.getUserAccountType() != UserAccountType.DRIVER) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Requesters cannot create offers");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // Check if contract is in a valid state for new offers
        if (contractService.getContractById(offerPostDTO.getContractId()).getContractStatus() == ContractStatus.ACCEPTED ||
            contractService.getContractById(offerPostDTO.getContractId()).getContractStatus() == ContractStatus.COMPLETED ||
            contractService.getContractById(offerPostDTO.getContractId()).getContractStatus() == ContractStatus.CANCELED ||
            contractService.getContractById(offerPostDTO.getContractId()).getContractStatus() == ContractStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot create offer for a contract that is " + contractService.getContractById(offerPostDTO.getContractId()).getContractStatus());
        }

        // Create the offer
        OfferGetDTO createdOffer = offerService.createOffer(offerPostDTO);

        // Create response with standard format
        Map<String, Object> response = new HashMap<>();
        response.put("offer", createdOffer);
        response.put("message", "Offer created successfully");
        response.put("timestamp", System.currentTimeMillis());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Delete an offer
     * 
     * Example call:
     * DELETE /api/v1/offers/789
     */
    @DeleteMapping("/api/v1/offers/{offerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteOffer(
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

        // Get the offer to check authorization
        OfferGetDTO offer = offerService.getOffer(offerId);
        if (offer == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Offer not found");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        // Verify user is authorized to delete the offer
        if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Drivers can only delete their own offers
            if (!offer.getDriver().getUserId().equals(userId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "You are not authorized to delete this offer");
                response.put("timestamp", System.currentTimeMillis());
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } else {
            // Requesters cannot delete offers
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Requesters cannot delete offers");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // Delete the offer
        offerService.deleteOffer(offerId);

        // Return success response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Offer deleted successfully");
        response.put("timestamp", System.currentTimeMillis());
        
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
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
    public ResponseEntity<Object> updateOfferStatus(
            @PathVariable Long offerId,
            @RequestParam OfferStatus status,
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

        // Get the offer to check authorization
        OfferGetDTO offer = offerService.getOffer(offerId);

        // Verify user is authorized to update the offer status
        if (authenticatedUser.getUserAccountType() == UserAccountType.DRIVER) {
            // Drivers can only update their own offers
            if (!offer.getDriver().getUserId().equals(userId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "You are not authorized to update this offer");
                response.put("timestamp", System.currentTimeMillis());
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // Drivers can only set status to DELETED
            if (status != OfferStatus.DELETED) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Drivers can only delete their offers");
                response.put("timestamp", System.currentTimeMillis());
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } else if (authenticatedUser.getUserAccountType() == UserAccountType.REQUESTER) {
            // Requesters can only accept offers for their contracts
            if (!offer.getContract().getRequesterId().equals(userId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "You are not authorized to update this offer");
                response.put("timestamp", System.currentTimeMillis());
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // Requesters can only set status to ACCEPTED
            if (status != OfferStatus.ACCEPTED) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Requesters can only accept offers");
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

        // Update the offer status
        OfferGetDTO updatedOffer = offerService.updateOfferStatus(offerId, status);

        // Return success response
        Map<String, Object> response = new HashMap<>();
        response.put("offer", updatedOffer);
        response.put("message", "Offer status updated successfully");
        response.put("timestamp", System.currentTimeMillis());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
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