package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.OfferGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.OfferPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.OfferService;

/**
 * Offer Controller
 * This class is responsible for handling all REST request that are related to
 * the offer management.
 */
@RestController
public class OfferController {

    private final OfferService offerService;

    OfferController(OfferService offerService) {
        this.offerService = offerService;
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
    public List<OfferGetDTO> getOffers(
            @RequestParam(required = false) Long contractId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) OfferStatus status) {
        return offerService.getOffers(contractId, driverId, status);
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
    public OfferGetDTO getOffer(@PathVariable Long offerId) {
        return offerService.getOffer(offerId);
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
} 