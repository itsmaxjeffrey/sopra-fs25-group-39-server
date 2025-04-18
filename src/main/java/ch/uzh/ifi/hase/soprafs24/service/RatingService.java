package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.security.authentication.controller.AuthController;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.repository.RatingRepository;

@Service
public class RatingService {

    private final AuthorizationService authorizationService;

    private final CarUpdater carUpdater;

    private final AuthController authController;

    @Autowired
    private RatingRepository ratingRepository;

    RatingService(AuthController authController, CarUpdater carUpdater, AuthorizationService authorizationService) {
        this.authController = authController;
        this.carUpdater = carUpdater;
        this.authorizationService = authorizationService;
    }
    
    public Rating getRatingById(Long ratingId) {
        // Implement the logic to retrieve a rating by its ID
        // This is just a placeholder implementation

        //authorize 
        if (ratingId == null) {
            throw new IllegalArgumentException("Rating ID cannot be null");
        }
        if (ratingId <= 0) {
            throw new IllegalArgumentException("Rating ID must be a positive number");
        }
        return ratingRepository.findByRatingId(ratingId);
    }
}
