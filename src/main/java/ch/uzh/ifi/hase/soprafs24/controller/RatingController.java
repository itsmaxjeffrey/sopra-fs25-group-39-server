package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.service.RatingService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPutDTO;
import org.springframework.web.bind.annotation.RequestHeader;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {
    
    private final RatingService ratingService;
    private final AuthorizationService authorizationService;

    // Error message constants
    private static final String ERROR_USER_NOT_AUTHORIZED = "User is not authorized";
    private static final String ERROR_RATING_NOT_FOUND = "Rating not found";
    private static final String MESSAGE_RATING_DELETED = "Rating deleted successfully";

    public RatingController(RatingService ratingService, AuthorizationService authorizationService) {
        this.ratingService = ratingService;
        this.authorizationService = authorizationService;
    }

    /**
     * Helper method to create a standardized response
     */
    private ResponseEntity<Object> createResponse(Object data, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        if (data != null) {
            if (data instanceof List) {
                response.put("ratings", data);
            } else {
                response.put("rating", data);
            }
        }
        if (message != null) {
            response.put("message", message);
        }
        response.put("timestamp", System.currentTimeMillis());
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRatingById(@PathVariable Long id, @RequestHeader("userId") Long userId, @RequestHeader("Authorization") String token) {
        if (authorizationService.authenticateUser(userId, token) == null) {
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        Rating rating = ratingService.getRatingById(id);
        if (rating == null) {
            return createResponse(null, ERROR_RATING_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return createResponse(rating, null, HttpStatus.OK);
    }

    /**
     * Get all ratings for a specific user
     * 
     * Example request:
     * GET /api/v1/users/123/ratings
     */
    @GetMapping("/users/{userId}/ratings")
    public ResponseEntity<Object> getUserRatings(
            @PathVariable Long userId,
            @RequestHeader("userId") Long requestUserId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        if (authorizationService.authenticateUser(requestUserId, token) == null) {
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        // Get ratings from service
        List<Rating> ratings = ratingService.getRatingsByUserId(userId);
        return createResponse(ratings, null, HttpStatus.OK);
    }

    /**
     * Get all ratings for a specific contract
     * 
     * Example request:
     * GET /api/v1/contracts/123/ratings
     */
    @GetMapping("/contracts/{contractId}/ratings")
    public ResponseEntity<Object> getContractRatings(
            @PathVariable Long contractId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        if (authorizationService.authenticateUser(userId, token) == null) {
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        // Get ratings from service
        List<Rating> ratings = ratingService.getRatingsByContractId(contractId);
        return createResponse(ratings, null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> createRating(
            @RequestBody RatingPostDTO ratingPostDTO,
            @RequestHeader("userId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        if (authorizationService.authenticateUser(userId, token) == null) {
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        try {
            // Create rating
            Rating rating = ratingService.createRating(ratingPostDTO, userId);
            return createResponse(rating, null, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return createResponse(null, e.getReason(), e.getStatus());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRating(
            @PathVariable Long id,
            @RequestBody RatingPutDTO ratingPutDTO,
            @RequestHeader("userId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        if (authorizationService.authenticateUser(userId, token) == null) {
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        try {
            // Update rating
            Rating rating = ratingService.updateRating(id, ratingPutDTO, userId);
            return createResponse(rating, null, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return createResponse(null, e.getReason(), e.getStatus());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRating(
            @PathVariable Long id,
            @RequestHeader("userId") Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        if (authorizationService.authenticateUser(userId, token) == null) {
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        try {
            // Delete rating
            ratingService.deleteRating(id, userId);
            return createResponse(null, MESSAGE_RATING_DELETED, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return createResponse(null, e.getReason(), e.getStatus());
        }
    }

    /**
     * Get average rating for a specific user
     * 
     * Example request:
     * GET /api/v1/users/123/average-rating
     */
    @GetMapping("/users/{userId}/average-rating")
    public ResponseEntity<Object> getUserAverageRating(
            @PathVariable Long userId,
            @RequestHeader("userId") Long requestUserId,
            @RequestHeader("Authorization") String token) {
        
        // Authenticate user
        if (authorizationService.authenticateUser(requestUserId, token) == null) {
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        // Get average rating from service
        Double averageRating = ratingService.getAverageRating(userId);
        return createResponse(averageRating, null, HttpStatus.OK);
    }
}
