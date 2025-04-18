package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.RatingService;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private RatingController ratingController;

    private Rating testRating;
    private User testUser;
    private final String validToken = "valid-token";
    private final Long validUserId = 1L;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test rating
        testRating = new Rating();
        testRating.setRatingId(1L);
        testRating.setRatingValue(5);
        testRating.setFlagIssues(false);
        testRating.setComment("Great service!");

        // Create test user
        testUser = new User();
        testUser.setUserId(validUserId);
    }

    @Test
    void getRatingById_validRequest_returnsRating() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(testUser);
        when(ratingService.getRatingById(1L)).thenReturn(testRating);

        // when
        ResponseEntity<Rating> response = ratingController.getRatingById(1L, validUserId, validToken);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testRating.getRatingId(), response.getBody().getRatingId());
        assertEquals(testRating.getRatingValue(), response.getBody().getRatingValue());
        assertEquals(testRating.getComment(), response.getBody().getComment());
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, times(1)).getRatingById(1L);
    }

    @Test
    void getRatingById_unauthorizedUser_throwsException() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(null);

        // when/then
        assertThrows(IllegalArgumentException.class, () -> {
            ratingController.getRatingById(1L, validUserId, validToken);
        });
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, never()).getRatingById(any());
    }

    @Test
    void getRatingById_invalidToken_throwsException() {
        // given
        when(authorizationService.authenticateUser(validUserId, "invalid-token")).thenReturn(null);

        // when/then
        assertThrows(IllegalArgumentException.class, () -> {
            ratingController.getRatingById(1L, validUserId, "invalid-token");
        });
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, "invalid-token");
        verify(ratingService, never()).getRatingById(any());
    }

    @Test
    void getRatingById_nonExistentRating_returnsNotFound() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(testUser);
        when(ratingService.getRatingById(99L)).thenReturn(null);

        // when
        ResponseEntity<Rating> response = ratingController.getRatingById(99L, validUserId, validToken);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, times(1)).getRatingById(99L);
    }
} 