package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.RatingService;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private RatingPostDTO testRatingPostDTO;
    private final String validToken = "valid-token";
    private final Long validUserId = 1L;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User();
        testUser.setUserId(validUserId);

        // Create test rating
        testRating = new Rating();
        testRating.setRatingId(1L);
        testRating.setRatingValue(5);
        testRating.setFlagIssues(false);
        testRating.setComment("Great service!");

        // Create test DTO
        testRatingPostDTO = new RatingPostDTO();
        testRatingPostDTO.setContractId(1L);
        testRatingPostDTO.setRatingValue(5);
        testRatingPostDTO.setFlagIssues(false);
        testRatingPostDTO.setComment("Great service!");
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRatingById_validRequest_returnsRating() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(testUser);
        when(ratingService.getRatingById(1L)).thenReturn(testRating);

        // when
        ResponseEntity<Object> response = ratingController.getRatingById(1L, validUserId, validToken);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testRating, responseBody.get("rating"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, times(1)).getRatingById(1L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRatingById_unauthorizedUser_throwsException() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(null);

        // when
        ResponseEntity<Object> response = ratingController.getRatingById(1L, validUserId, validToken);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User is not authorized", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, never()).getRatingById(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRatingById_invalidToken_throwsException() {
        // given
        when(authorizationService.authenticateUser(validUserId, "invalid-token")).thenReturn(null);

        // when
        ResponseEntity<Object> response = ratingController.getRatingById(1L, validUserId, "invalid-token");

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User is not authorized", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, "invalid-token");
        verify(ratingService, never()).getRatingById(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRatingById_nonExistentRating_returnsNotFound() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(testUser);
        when(ratingService.getRatingById(99L)).thenReturn(null);

        // when
        ResponseEntity<Object> response = ratingController.getRatingById(99L, validUserId, validToken);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Rating not found", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, times(1)).getRatingById(99L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserRatings_validRequest_returnsRatings() {
        // given
        List<Rating> ratings = new ArrayList<>();
        ratings.add(testRating);
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(testUser);
        when(ratingService.getRatingsByUserId(validUserId)).thenReturn(ratings);

        // when
        ResponseEntity<Object> response = ratingController.getUserRatings(validUserId, validUserId, validToken);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(ratings, responseBody.get("ratings"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, times(1)).getRatingsByUserId(validUserId);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserRatings_unauthorizedUser_throwsException() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(null);

        // when
        ResponseEntity<Object> response = ratingController.getUserRatings(validUserId, validUserId, validToken);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User is not authorized", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, never()).getRatingsByUserId(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getContractRatings_validRequest_returnsRatings() {
        // given
        List<Rating> ratings = new ArrayList<>();
        ratings.add(testRating);
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(testUser);
        when(ratingService.getRatingsByContractId(1L)).thenReturn(ratings);

        // when
        ResponseEntity<Object> response = ratingController.getContractRatings(1L, validUserId, validToken);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(ratings, responseBody.get("ratings"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, times(1)).getRatingsByContractId(1L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getContractRatings_unauthorizedUser_throwsException() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(null);

        // when
        ResponseEntity<Object> response = ratingController.getContractRatings(1L, validUserId, validToken);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User is not authorized", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, never()).getRatingsByContractId(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void createRating_validRequest_createsRating() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(testUser);
        when(ratingService.createRating(testRatingPostDTO, validUserId)).thenReturn(testRating);

        // when
        ResponseEntity<Object> response = ratingController.createRating(testRatingPostDTO, validUserId, validToken);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testRating, responseBody.get("rating"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, times(1)).createRating(testRatingPostDTO, validUserId);
    }

    @Test
    @SuppressWarnings("unchecked")
    void createRating_unauthorizedUser_throwsException() {
        // given
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(null);

        // when
        ResponseEntity<Object> response = ratingController.createRating(testRatingPostDTO, validUserId, validToken);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User is not authorized", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, never()).createRating(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void createRating_invalidInput_throwsException() {
        // given
        testRatingPostDTO.setRatingValue(6); // Invalid rating value
        when(authorizationService.authenticateUser(validUserId, validToken)).thenReturn(testUser);
        when(ratingService.createRating(testRatingPostDTO, validUserId))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating value must be between 1 and 5"));

        // when
        ResponseEntity<Object> response = ratingController.createRating(testRatingPostDTO, validUserId, validToken);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Rating value must be between 1 and 5", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
        
        verify(authorizationService, times(1)).authenticateUser(validUserId, validToken);
        verify(ratingService, times(1)).createRating(testRatingPostDTO, validUserId);
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateRating_validInput_returnsUpdatedRating() {
        // given
        RatingPutDTO updateDTO = new RatingPutDTO();
        updateDTO.setRatingValue(4);
        updateDTO.setFlagIssues(true);
        updateDTO.setComment("Updated comment");

        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(testUser);
        when(ratingService.updateRating(1L, updateDTO, 1L)).thenReturn(testRating);

        // when
        ResponseEntity<Object> response = ratingController.updateRating(1L, updateDTO, 1L, "valid-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testRating, responseBody.get("rating"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateRating_unauthorizedUser_returnsUnauthorized() {
        // given
        RatingPutDTO updateDTO = new RatingPutDTO();
        when(authorizationService.authenticateUser(1L, "invalid-token")).thenReturn(null);

        // when
        ResponseEntity<Object> response = ratingController.updateRating(1L, updateDTO, 1L, "invalid-token");

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User is not authorized", responseBody.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateRating_nonExistentRating_returnsNotFound() {
        // given
        RatingPutDTO updateDTO = new RatingPutDTO();
        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(testUser);
        when(ratingService.updateRating(1L, updateDTO, 1L))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found"));

        // when
        ResponseEntity<Object> response = ratingController.updateRating(1L, updateDTO, 1L, "valid-token");

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Rating not found", responseBody.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteRating_validInput_returnsSuccess() {
        // given
        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(testUser);
        doNothing().when(ratingService).deleteRating(1L, 1L);

        // when
        ResponseEntity<Object> response = ratingController.deleteRating(1L, 1L, "valid-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Rating deleted successfully", responseBody.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteRating_unauthorizedUser_returnsUnauthorized() {
        // given
        when(authorizationService.authenticateUser(1L, "invalid-token")).thenReturn(null);

        // when
        ResponseEntity<Object> response = ratingController.deleteRating(1L, 1L, "invalid-token");

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User is not authorized", responseBody.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteRating_nonExistentRating_returnsNotFound() {
        // given
        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(testUser);
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found"))
            .when(ratingService).deleteRating(1L, 1L);

        // when
        ResponseEntity<Object> response = ratingController.deleteRating(1L, 1L, "valid-token");

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Rating not found", responseBody.get("message"));
    }
} 