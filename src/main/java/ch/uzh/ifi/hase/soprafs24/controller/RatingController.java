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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.RatingDTOMapper;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private static final Logger log = LoggerFactory.getLogger(RatingController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RatingService ratingService;
    private final AuthorizationService authorizationService;

    // Error message constants
    private static final String ERROR_USER_NOT_AUTHORIZED = "User is not authorized";
    private static final String ERROR_RATING_NOT_FOUND = "Rating not found";
    private static final String MESSAGE_RATING_DELETED = "Rating deleted successfully";
    private static final String UNEXPECTED_ERROR_MSG = "An unexpected error occurred.";

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
        log.info("Received request to get rating by ID: {}", id);
        if (authorizationService.authenticateUser(userId, token) == null) {
            log.warn("Unauthorized attempt to get rating ID: {} by user ID: {}", id, userId);
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        Rating rating = ratingService.getRatingById(id);
        if (rating == null) {
            log.warn("Rating not found for ID: {}", id);
            return createResponse(null, ERROR_RATING_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        RatingDTO ratingDTO = RatingDTOMapper.INSTANCE.convertEntityToRatingDTO(rating);
        log.info("Returning rating ID: {}", id);
        return createResponse(ratingDTO, null, HttpStatus.OK);
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
        log.info("Received request to get ratings for user ID: {}", userId);
        // Authenticate user
        if (authorizationService.authenticateUser(requestUserId, token) == null) {
            log.warn("Unauthorized attempt to get ratings for user ID: {} by request user ID: {}", userId, requestUserId);
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        // Get ratings from service
        List<Rating> ratings = ratingService.getRatingsByUserId(userId);
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(RatingDTOMapper.INSTANCE::convertEntityToRatingDTO)
                .collect(Collectors.toList());
        log.info("Returning {} ratings for user ID: {}", ratingDTOs.size(), userId);
        return createResponse(ratingDTOs, null, HttpStatus.OK);
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
        log.info("Received request to get ratings for contract ID: {}", contractId);
        // Authenticate user
        if (authorizationService.authenticateUser(userId, token) == null) {
            log.warn("Unauthorized attempt to get ratings for contract ID: {} by user ID: {}", contractId, userId);
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        // Get ratings from service
        List<Rating> ratings = ratingService.getRatingsByContractId(contractId);
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(RatingDTOMapper.INSTANCE::convertEntityToRatingDTO)
                .collect(Collectors.toList());
        log.info("Returning {} ratings for contract ID: {}", ratingDTOs.size(), contractId);
        return createResponse(ratingDTOs, null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> createRating(
            @RequestBody RatingPostDTO ratingPostDTO,
            @RequestHeader("userId") Long userId,
            @RequestHeader("Authorization") String token) {
        String dtoString = "DTO unavailable";
        try {
            dtoString = objectMapper.writeValueAsString(ratingPostDTO);
        } catch (Exception e) {
            log.warn("Could not serialize RatingPostDTO for logging");
        }
        log.info("Received request to create rating from user ID: {} for contract ID: {}. DTO: {}", userId, ratingPostDTO.getContractId(), dtoString);

        // Authenticate user
        if (authorizationService.authenticateUser(userId, token) == null) {
            log.warn("Unauthorized attempt to create rating by user ID: {}", userId);
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        try {
            // Create rating
            Rating rating = ratingService.createRating(ratingPostDTO, userId);
            // Map Rating entity to RatingDTO using the specific mapper
            RatingDTO ratingDTO = RatingDTOMapper.INSTANCE.convertEntityToRatingDTO(rating);
            log.info("Successfully created rating with ID: {} for contract ID: {}", rating.getRatingId(), ratingPostDTO.getContractId());
            // Return the DTO in the response
            return createResponse(ratingDTO, null, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            log.error("Error creating rating for contract ID: {}. Reason: {}", ratingPostDTO.getContractId(), e.getReason(), e);
            return createResponse(null, e.getReason(), e.getStatus());
        } catch (Exception e) {
            log.error("Unexpected error creating rating for contract ID: {}", ratingPostDTO.getContractId(), e);
            return createResponse(null, UNEXPECTED_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRating(
            @PathVariable Long id,
            @RequestBody RatingPutDTO ratingPutDTO,
            @RequestHeader("userId") Long userId,
            @RequestHeader("Authorization") String token) {
        String dtoString = "DTO unavailable";
        try {
            dtoString = objectMapper.writeValueAsString(ratingPutDTO);
        } catch (Exception e) {
            log.warn("Could not serialize RatingPutDTO for logging");
        }
        log.info("Received request to update rating ID: {} by user ID: {}. DTO: {}", id, userId, dtoString);

        // Authenticate user
        if (authorizationService.authenticateUser(userId, token) == null) {
            log.warn("Unauthorized attempt to update rating ID: {} by user ID: {}", id, userId);
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        try {
            // Update rating
            Rating rating = ratingService.updateRating(id, ratingPutDTO, userId);
            RatingDTO ratingDTO = RatingDTOMapper.INSTANCE.convertEntityToRatingDTO(rating);
            log.info("Successfully updated rating ID: {}", id);
            return createResponse(ratingDTO, null, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            log.error("Error updating rating ID: {}. Reason: {}", id, e.getReason(), e);
            return createResponse(null, e.getReason(), e.getStatus());
        } catch (Exception e) {
            log.error("Unexpected error updating rating ID: {}", id, e);
            return createResponse(null, UNEXPECTED_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRating(
            @PathVariable Long id,
            @RequestHeader("userId") Long userId,
            @RequestHeader("Authorization") String token) {
        log.info("Received request to delete rating ID: {} by user ID: {}", id, userId);
        // Authenticate user
        if (authorizationService.authenticateUser(userId, token) == null) {
            log.warn("Unauthorized attempt to delete rating ID: {} by user ID: {}", id, userId);
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        try {
            // Delete rating
            ratingService.deleteRating(id, userId);
            log.info("Successfully deleted rating ID: {}", id);
            return createResponse(null, MESSAGE_RATING_DELETED, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            log.error("Error deleting rating ID: {}. Reason: {}", id, e.getReason(), e);
            return createResponse(null, e.getReason(), e.getStatus());
        } catch (Exception e) {
            log.error("Unexpected error deleting rating ID: {}", id, e);
            return createResponse(null, UNEXPECTED_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
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
        log.info("Received request to get average rating for user ID: {}", userId);
        // Authenticate user
        if (authorizationService.authenticateUser(requestUserId, token) == null) {
            log.warn("Unauthorized attempt to get average rating for user ID: {} by request user ID: {}", userId, requestUserId);
            return createResponse(null, ERROR_USER_NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        // Get average rating from service
        Double averageRating = ratingService.getAverageRating(userId);
        log.info("Returning average rating for user ID {}: {}", userId, averageRating);
        return createResponse(averageRating, null, HttpStatus.OK);
    }
}
