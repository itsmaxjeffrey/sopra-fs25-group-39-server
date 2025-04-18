package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.service.RatingService;

import org.springframework.web.bind.annotation.RequestHeader;

import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {
    
    private final RatingService ratingService;
    private final AuthorizationService authorizationService;

    public RatingController(RatingService ratingService, AuthorizationService authorizationService) {
        this.ratingService = ratingService;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable Long id, @RequestHeader("userId") Long userId, @RequestHeader("Authorization") String token) {

        if (authorizationService.authenticateUser(userId, token) == null) {
            throw new IllegalArgumentException("User is not authorized");
        }
        Rating rating = ratingService.getRatingById(id);
        return ResponseEntity.ok(rating);
    }
}
