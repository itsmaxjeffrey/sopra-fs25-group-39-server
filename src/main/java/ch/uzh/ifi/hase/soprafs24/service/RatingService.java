package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.repository.RatingRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import java.util.List;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Service class for handling rating-related operations.
 * Provides methods for managing and retrieving ratings.
 */
@Service
public class RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RatingRepository ratingRepository;
    private final ContractService contractService;
    private final UserRepository userRepository;

    public RatingService(
            RatingRepository ratingRepository,
            ContractService contractService,
            UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.contractService = contractService;
        this.userRepository = userRepository;
    }
    
    /**
     * Retrieves a rating by its ID.
     * 
     * @param ratingId The ID of the rating to retrieve
     * @return The rating with the specified ID
     * @throws IllegalArgumentException if ratingId is null or not positive
     */
    public Rating getRatingById(Long ratingId) {
        log.info("Attempting to retrieve rating by ID: {}", ratingId);
        if (ratingId == null) {
            log.warn("Attempted to get rating with null ID.");
            throw new IllegalArgumentException("Rating ID cannot be null");
        }
        if (ratingId <= 0) {
            log.warn("Attempted to get rating with non-positive ID: {}", ratingId);
            throw new IllegalArgumentException("Rating ID must be a positive number");
        }
        Rating rating = ratingRepository.findByRatingId(ratingId);
        if (rating != null) {
            log.info("Found rating with ID: {}", ratingId);
        } else {
            log.warn("Rating not found for ID: {}", ratingId);
        }
        return rating;
    }

    /**
     * Creates a new rating for a completed contract.
     * 
     * @param ratingPostDTO The rating data
     * @param requesterId The ID of the requester creating the rating
     * @return The created rating
     * @throws ResponseStatusException if:
     *         - Contract is not in COMPLETED state
     *         - User is not a requester
     *         - Contract does not belong to the requester
     *         - Rating value is not between 1 and 5
     *         - Rating already exists for this user and contract
     */
    public Rating createRating(RatingPostDTO ratingPostDTO, Long requesterId) {
        String dtoString = "DTO unavailable";
        try {
            dtoString = objectMapper.writeValueAsString(ratingPostDTO);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize RatingPostDTO for logging");
        }
        log.info("Attempting to create rating by requester ID: {} for contract ID: {}. DTO: {}", requesterId, ratingPostDTO.getContractId(), dtoString);

        // Validate requester
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> {
                log.warn("Requester not found for ID: {}", requesterId);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            });
        if (requester.getUserAccountType() != UserAccountType.REQUESTER) {
            log.warn("User ID: {} is not a requester, cannot create rating.", requesterId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only requesters can create ratings");
        }

        // Get and validate contract
        Contract contract = contractService.getContractById(ratingPostDTO.getContractId());
        try {
            log.debug("Found contract: {}", objectMapper.writeValueAsString(contract));
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize Contract for logging");
        }

        // Add explicit check for COMPLETED status
        if (contract.getContractStatus() != ContractStatus.COMPLETED) {
            log.warn("Contract ID: {} is not in COMPLETED state. Current state: {}", contract.getContractId(), contract.getContractStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contract must be in COMPLETED state to be rated.");
        }

        if (!contract.getRequester().getUserId().equals(requesterId)) {
            log.warn("Requester ID: {} does not match contract requester ID: {}", requesterId, contract.getRequester().getUserId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only rate your own contracts");
        }

        // Check for existing rating
        Rating existingRating = ratingRepository.findByContract_ContractIdAndFromUser_UserId(
            ratingPostDTO.getContractId(), requesterId);
        if (existingRating != null) {
            log.warn("Duplicate rating attempt by requester ID: {} for contract ID: {}", requesterId, ratingPostDTO.getContractId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already rated this contract");
        }

        // Validate rating value
        if (ratingPostDTO.getRatingValue() == null || ratingPostDTO.getRatingValue() < 1 || ratingPostDTO.getRatingValue() > 5) {
            log.warn("Invalid rating value: {} provided by requester ID: {}", ratingPostDTO.getRatingValue(), requesterId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating value must be between 1 and 5");
        }

        // Create rating
        Rating rating = new Rating();
        rating.setFromUser(requester);
        rating.setToUser(contract.getDriver());
        rating.setContract(contract);
        rating.setRatingValue(ratingPostDTO.getRatingValue());
        rating.setFlagIssues(ratingPostDTO.isFlagIssues());
        rating.setComment(ratingPostDTO.getComment());
        try {
            log.debug("Prepared rating entity: {}", objectMapper.writeValueAsString(rating));
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize Rating entity for logging");
        }

        // Save rating
        Rating savedRating = ratingRepository.save(rating);
        log.info("Saved rating with ID: {} for contract ID: {}", savedRating.getRatingId(), contract.getContractId());

        // Update contract status to FINALIZED using the specific status update method
        try {
            contractService.updateContractStatus(contract.getContractId(), ContractStatus.FINALIZED);
            log.info("Updated contract ID: {} status to FINALIZED", contract.getContractId());
        } catch (Exception e) {
            log.error("Failed to update contract ID: {} status to FINALIZED after rating.", contract.getContractId(), e);
        }

        return savedRating;
    }

    /**
     * Retrieves all ratings for a specific user.
     * 
     * @param userId The ID of the user
     * @return List of ratings for the user
     */
    public List<Rating> getRatingsByUserId(Long userId) {
        log.info("Attempting to retrieve ratings for user ID: {}", userId);
        if (userId == null) {
            log.warn("Attempted to get ratings with null user ID.");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userId <= 0) {
            log.warn("Attempted to get ratings with non-positive user ID: {}", userId);
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        List<Rating> ratings = ratingRepository.findByToUser_UserId(userId);
        log.info("Found {} ratings for user ID: {}", ratings.size(), userId);
        try {
            log.debug("Ratings found for user ID {}: {}", userId, objectMapper.writeValueAsString(ratings));
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize ratings list for logging");
        }
        return ratings;
    }

    /**
     * Retrieves all ratings for a specific contract.
     * 
     * @param contractId The ID of the contract
     * @return List of ratings for the contract
     */
    public List<Rating> getRatingsByContractId(Long contractId) {
        log.info("Attempting to retrieve ratings for contract ID: {}", contractId);
        if (contractId == null) {
            log.warn("Attempted to get ratings with null contract ID.");
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (contractId <= 0) {
            log.warn("Attempted to get ratings with non-positive contract ID: {}", contractId);
            throw new IllegalArgumentException("Contract ID must be a positive number");
        }
        List<Rating> ratings = ratingRepository.findByContract_ContractId(contractId);
        log.info("Found {} ratings for contract ID: {}", ratings.size(), contractId);
        try {
            log.debug("Ratings found for contract ID {}: {}", contractId, objectMapper.writeValueAsString(ratings));
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize ratings list for logging");
        }
        return ratings;
    }

    /**
     * Updates an existing rating.
     * 
     * @param ratingId The ID of the rating to update
     * @param ratingPutDTO The updated rating data
     * @param userId The ID of the user making the update
     * @return The updated rating
     * @throws ResponseStatusException if:
     *         - Rating doesn't exist
     *         - User is not the original rater
     *         - Rating value is not between 1 and 5
     */
    public Rating updateRating(Long ratingId, RatingPutDTO ratingPutDTO, Long userId) {
        String dtoString = "DTO unavailable";
        try {
            dtoString = objectMapper.writeValueAsString(ratingPutDTO);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize RatingPutDTO for logging");
        }
        log.info("Attempting to update rating ID: {} by user ID: {}. DTO: {}", ratingId, userId, dtoString);

        // Get and validate rating
        Rating rating = getRatingById(ratingId);
        if (rating == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found");
        }

        // Validate user is the original rater
        if (!rating.getFromUser().getUserId().equals(userId)) {
            log.warn("User ID: {} attempted to update rating ID: {} owned by user ID: {}", userId, ratingId, rating.getFromUser().getUserId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own ratings");
        }

        // Validate rating value
        if (ratingPutDTO.getRatingValue() != null) {
            if (ratingPutDTO.getRatingValue() < 1 || ratingPutDTO.getRatingValue() > 5) {
                log.warn("Invalid rating value: {} provided for update by user ID: {}", ratingPutDTO.getRatingValue(), userId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating value must be between 1 and 5");
            }
            rating.setRatingValue(ratingPutDTO.getRatingValue());
        }

        // Update other fields
        rating.setFlagIssues(ratingPutDTO.isFlagIssues());
        if (ratingPutDTO.getComment() != null) {
            rating.setComment(ratingPutDTO.getComment());
        }
        try {
            log.debug("Prepared updated rating entity: {}", objectMapper.writeValueAsString(rating));
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize updated Rating entity for logging");
        }

        // Save updated rating
        Rating updatedRating = ratingRepository.save(rating);
        log.info("Successfully updated rating ID: {}", ratingId);
        return updatedRating;
    }

    /**
     * Deletes a rating.
     * 
     * @param ratingId The ID of the rating to delete
     * @param userId The ID of the user making the delete request
     * @throws ResponseStatusException if:
     *         - Rating doesn't exist
     *         - User is not the original rater
     *         - Failed to revert contract status
     */
    public void deleteRating(Long ratingId, Long userId) {
        log.info("Attempting to delete rating ID: {} by user ID: {}", ratingId, userId);
        // Get and validate rating
        Rating rating = getRatingById(ratingId);
        if (rating == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found");
        }

        // Validate user is the original rater
        if (!rating.getFromUser().getUserId().equals(userId)) {
            log.warn("User ID: {} attempted to delete rating ID: {} owned by user ID: {}", userId, ratingId, rating.getFromUser().getUserId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own ratings");
        }

        // Get the contract and attempt to update its status
        Contract contract = rating.getContract();
        log.warn("Deleting rating ID: {}. Attempting to revert associated contract ID: {} status to COMPLETED.", ratingId, contract.getContractId());
        try {
            contractService.updateContractStatus(contract.getContractId(), ContractStatus.COMPLETED);
            log.info("Reverted contract ID: {} status to COMPLETED", contract.getContractId());
        } catch (Exception e) {
            log.error("Failed to revert contract ID: {} status to COMPLETED during rating deletion. Rating ID: {} will not be deleted. Error: {}", 
                      contract.getContractId(), ratingId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update contract status during rating deletion. Rating not deleted.", e);
        }

        // Delete rating ONLY if contract status revert was successful
        ratingRepository.delete(rating);
        log.info("Successfully deleted rating ID: {}", ratingId);
    }

    /**
     * Calculates the average rating for a user.
     * 
     * @param userId The ID of the user
     * @return The average rating, or null if no ratings exist
     * @throws IllegalArgumentException if userId is null or not positive
     */
    public Double getAverageRating(Long userId) {
        log.info("Calculating average rating for user ID: {}", userId);
        if (userId == null) {
            log.warn("Attempted to calculate average rating with null user ID.");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userId <= 0) {
            log.warn("Attempted to calculate average rating with non-positive user ID: {}", userId);
            throw new IllegalArgumentException("User ID must be a positive number");
        }

        List<Rating> ratings = getRatingsByUserId(userId);
        if (ratings.isEmpty()) {
            log.info("No ratings found for user ID: {}, returning null average.", userId);
            return null;
        }

        Double average = ratings.stream()
            .filter(r -> r.getRatingValue() != null)
            .mapToInt(Rating::getRatingValue)
            .average()
            .orElse(Double.NaN);

        if (Double.isNaN(average)) {
            log.warn("Average calculation resulted in NaN for user ID: {}.", userId);
            return null;
        }

        log.info("Calculated average rating for user ID {}: {}", userId, average);
        return average;
    }
}
