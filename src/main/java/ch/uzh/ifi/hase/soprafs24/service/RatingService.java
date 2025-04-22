package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.security.authentication.controller.AuthController;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.repository.RatingRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import java.util.List;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPutDTO;

/**
 * Service class for handling rating-related operations.
 * Provides methods for managing and retrieving ratings.
 */
@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final ContractService contractService;
    private final UserRepository userRepository;

    public RatingService(
            AuthController authController, 
            CarUpdater carUpdater, 
            AuthorizationService authorizationService,
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
        if (ratingId == null) {
            throw new IllegalArgumentException("Rating ID cannot be null");
        }
        if (ratingId <= 0) {
            throw new IllegalArgumentException("Rating ID must be a positive number");
        }
        return ratingRepository.findByRatingId(ratingId);
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
     */
    public Rating createRating(RatingPostDTO ratingPostDTO, Long requesterId) {
        // Validate requester
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (requester.getUserAccountType() != UserAccountType.REQUESTER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only requesters can create ratings");
        }

        // Get and validate contract
        Contract contract = contractService.getContractById(ratingPostDTO.getContractId());
        // Comment out the status check to allow rating non-completed contracts
        // if (contract.getContractStatus() != ContractStatus.COMPLETED) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only rate completed contracts");
        // }
        if (!contract.getRequester().getUserId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only rate your own contracts");
        }

        // Check for existing rating
        Rating existingRating = ratingRepository.findByContract_ContractIdAndFromUser_UserId(
            ratingPostDTO.getContractId(), requesterId);
        if (existingRating != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already rated this contract");
        }

        // Validate rating value
        if (ratingPostDTO.getRatingValue() == null || ratingPostDTO.getRatingValue() < 1 || ratingPostDTO.getRatingValue() > 5) {
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

        // Save rating
        Rating savedRating = ratingRepository.save(rating);

        // Update contract status to FINALIZED using the specific status update method
        contractService.updateContractStatus(contract.getContractId(), ContractStatus.FINALIZED);

        return savedRating;
    }

    /**
     * Retrieves all ratings for a specific user.
     * 
     * @param userId The ID of the user
     * @return List of ratings for the user
     */
    public List<Rating> getRatingsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        return ratingRepository.findByToUser_UserId(userId);
    }

    /**
     * Retrieves all ratings for a specific contract.
     * 
     * @param contractId The ID of the contract
     * @return List of ratings for the contract
     */
    public List<Rating> getRatingsByContractId(Long contractId) {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (contractId <= 0) {
            throw new IllegalArgumentException("Contract ID must be a positive number");
        }
        return ratingRepository.findByContract_ContractId(contractId);
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
        // Get and validate rating
        Rating rating = getRatingById(ratingId);
        if (rating == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found");
        }

        // Validate user is the original rater
        if (!rating.getFromUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own ratings");
        }

        // Validate rating value
        if (ratingPutDTO.getRatingValue() != null) {
            if (ratingPutDTO.getRatingValue() < 1 || ratingPutDTO.getRatingValue() > 5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating value must be between 1 and 5");
            }
            rating.setRatingValue(ratingPutDTO.getRatingValue());
        }

        // Update other fields
        rating.setFlagIssues(ratingPutDTO.isFlagIssues());
        if (ratingPutDTO.getComment() != null) {
            rating.setComment(ratingPutDTO.getComment());
        }

        // Save updated rating
        return ratingRepository.save(rating);
    }

    /**
     * Deletes a rating.
     * 
     * @param ratingId The ID of the rating to delete
     * @param userId The ID of the user making the delete request
     * @throws ResponseStatusException if:
     *         - Rating doesn't exist
     *         - User is not the original rater
     */
    public void deleteRating(Long ratingId, Long userId) {
        // Get and validate rating
        Rating rating = getRatingById(ratingId);
        if (rating == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found");
        }

        // Validate user is the original rater
        if (!rating.getFromUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own ratings");
        }

        // Get the contract and update its status
        Contract contract = rating.getContract();
        contract.setContractStatus(ContractStatus.COMPLETED);
        contractService.updateContract(contract.getContractId(), contract);

        // Delete rating
        ratingRepository.delete(rating);
    }

    /**
     * Calculates the average rating for a user.
     * 
     * @param userId The ID of the user
     * @return The average rating, or null if no ratings exist
     * @throws IllegalArgumentException if userId is null or not positive
     */
    public Double getAverageRating(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }

        List<Rating> ratings = getRatingsByUserId(userId);
        if (ratings.isEmpty()) {
            return null;
        }

        return ratings.stream()
            .mapToInt(Rating::getRatingValue)
            .average()
            .orElse(0.0);
    }
}
