package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.security.authentication.controller.AuthController;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.repository.RatingRepository;

/**
 * Service class for handling rating-related operations.
 * Provides methods for managing and retrieving ratings.
 */
@Service
public class RatingService {
    private final RatingRepository ratingRepository;

    public RatingService(
            AuthController authController, 
            CarUpdater carUpdater, 
            AuthorizationService authorizationService,
            RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
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
}
