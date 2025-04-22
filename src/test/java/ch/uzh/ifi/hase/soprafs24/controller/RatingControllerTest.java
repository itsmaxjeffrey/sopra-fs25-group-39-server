package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.service.RatingService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private RatingController ratingController;

    private Rating testRating;
    private Requester testRequester;
    private Driver testDriver;
    private Contract testContract;
    private RatingPostDTO testRatingPostDTO;
    private RatingPutDTO testRatingPutDTO;
    private RatingDTO testRatingDTO;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup test requester
        testRequester = new Requester();
        testRequester.setUserId(1L);

        // Setup test driver
        testDriver = new Driver();
        testDriver.setUserId(2L);

        // Setup test contract
        testContract = new Contract();
        testContract.setContractId(1L);
        testContract.setRequester(testRequester);
        testContract.setDriver(testDriver);

        // Setup test rating
        testRating = new Rating();
        testRating.setRatingId(1L);
        testRating.setFromUser(testRequester);
        testRating.setToUser(testDriver);
        testRating.setContract(testContract);
        testRating.setRatingValue(5);
        testRating.setFlagIssues(false);
        testRating.setComment("Great service!");

        // Setup test DTOs
        testRatingPostDTO = new RatingPostDTO();
        testRatingPostDTO.setContractId(1L);
        testRatingPostDTO.setRatingValue(5);
        testRatingPostDTO.setFlagIssues(false);
        testRatingPostDTO.setComment("Great service!");

        testRatingPutDTO = new RatingPutDTO();
        testRatingPutDTO.setRatingValue(4);
        testRatingPutDTO.setFlagIssues(true);
        testRatingPutDTO.setComment("Updated comment");

        // Create expected RatingDTO from testRating
        testRatingDTO = new RatingDTO();
        testRatingDTO.setRatingId(1L);
        testRatingDTO.setFromUserId(1L);
        testRatingDTO.setToUserId(2L);
        testRatingDTO.setContractId(1L);
        testRatingDTO.setRatingValue(5);
        testRatingDTO.setFlagIssues(false);
        testRatingDTO.setComment("Great service!");

        // Mock service responses
        when(ratingService.getRatingById(any())).thenReturn(testRating);
        when(ratingService.createRating(any(), any())).thenReturn(testRating);
        Rating updatedRating = new Rating();
        updatedRating.setRatingId(1L);
        updatedRating.setFromUser(testRequester);
        updatedRating.setToUser(testDriver);
        updatedRating.setContract(testContract);
        updatedRating.setRatingValue(4);
        updatedRating.setFlagIssues(true);
        updatedRating.setComment("Updated comment");
        when(ratingService.updateRating(eq(1L), any(RatingPutDTO.class), eq(1L))).thenReturn(updatedRating);

        when(ratingService.getRatingsByUserId(any())).thenReturn(Arrays.asList(testRating));
        when(ratingService.getRatingsByContractId(any())).thenReturn(Arrays.asList(testRating));
        when(ratingService.getAverageRating(any())).thenReturn(5.0);

        // Mock authorization
        when(authorizationService.authenticateUser(any(), any())).thenReturn(testRequester);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRatingById_validRequest_returnsRatingDTO() throws Exception {
        ResponseEntity<Object> response = ratingController.getRatingById(1L, 1L, "token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        String expectedJson = objectMapper.writeValueAsString(testRatingDTO);
        String actualJson = objectMapper.writeValueAsString(body.get("rating"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRatingById_unauthorizedUser_throwsException() {
        when(authorizationService.authenticateUser(any(), any())).thenReturn(null);
        ResponseEntity<Object> response = ratingController.getRatingById(1L, 1L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("User is not authorized", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRatingById_nonExistentRating_returnsNotFound() {
        when(ratingService.getRatingById(any())).thenReturn(null);
        ResponseEntity<Object> response = ratingController.getRatingById(1L, 1L, "token");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Rating not found", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void createRating_validRequest_createsAndReturnsRatingDTO() throws Exception {
        ResponseEntity<Object> response = ratingController.createRating(testRatingPostDTO, 1L, "token");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        String expectedJson = objectMapper.writeValueAsString(testRatingDTO);
        String actualJson = objectMapper.writeValueAsString(body.get("rating"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    @SuppressWarnings("unchecked")
    void createRating_unauthorizedUser_throwsException() {
        when(authorizationService.authenticateUser(any(), any())).thenReturn(null);
        ResponseEntity<Object> response = ratingController.createRating(testRatingPostDTO, 1L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("User is not authorized", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void createRating_invalidInput_returnsBadRequest() {
        when(ratingService.createRating(any(), any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input"));
        ResponseEntity<Object> response = ratingController.createRating(testRatingPostDTO, 1L, "token");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Invalid input", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateRating_validRequest_updatesAndReturnsRatingDTO() throws Exception {
        ResponseEntity<Object> response = ratingController.updateRating(1L, testRatingPutDTO, 1L, "token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);

        RatingDTO expectedUpdatedDTO = new RatingDTO();
        expectedUpdatedDTO.setRatingId(1L);
        expectedUpdatedDTO.setFromUserId(1L);
        expectedUpdatedDTO.setToUserId(2L);
        expectedUpdatedDTO.setContractId(1L);
        expectedUpdatedDTO.setRatingValue(4);
        expectedUpdatedDTO.setFlagIssues(true);
        expectedUpdatedDTO.setComment("Updated comment");

        String expectedJson = objectMapper.writeValueAsString(expectedUpdatedDTO);
        String actualJson = objectMapper.writeValueAsString(body.get("rating"));
        assertEquals(expectedJson, actualJson);
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateRating_unauthorizedUser_throwsException() {
        when(authorizationService.authenticateUser(any(), any())).thenReturn(null);
        ResponseEntity<Object> response = ratingController.updateRating(1L, testRatingPutDTO, 1L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("User is not authorized", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteRating_validRequest_deletesRating() {
        ResponseEntity<Object> response = ratingController.deleteRating(1L, 1L, "token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Rating deleted successfully", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteRating_unauthorizedUser_throwsException() {
        when(authorizationService.authenticateUser(any(), any())).thenReturn(null);
        ResponseEntity<Object> response = ratingController.deleteRating(1L, 1L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("User is not authorized", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserRatings_validRequest_returnsRatingDTOList() throws Exception {
        ResponseEntity<Object> response = ratingController.getUserRatings(2L, 1L, "token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        List<RatingDTO> ratings = (List<RatingDTO>) body.get("ratings");
        assertNotNull(ratings);
        assertEquals(1, ratings.size());

        String expectedJson = objectMapper.writeValueAsString(Arrays.asList(testRatingDTO));
        String actualJson = objectMapper.writeValueAsString(ratings);
        assertEquals(expectedJson, actualJson);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserRatings_unauthorizedUser_throwsException() {
        when(authorizationService.authenticateUser(any(), any())).thenReturn(null);
        ResponseEntity<Object> response = ratingController.getUserRatings(2L, 1L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("User is not authorized", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getContractRatings_validRequest_returnsRatingDTOList() throws Exception {
        ResponseEntity<Object> response = ratingController.getContractRatings(1L, 1L, "token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        List<RatingDTO> ratings = (List<RatingDTO>) body.get("ratings");
        assertNotNull(ratings);
        assertEquals(1, ratings.size());

        String expectedJson = objectMapper.writeValueAsString(Arrays.asList(testRatingDTO));
        String actualJson = objectMapper.writeValueAsString(ratings);
        assertEquals(expectedJson, actualJson);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getContractRatings_unauthorizedUser_throwsException() {
        when(authorizationService.authenticateUser(any(), any())).thenReturn(null);
        ResponseEntity<Object> response = ratingController.getContractRatings(1L, 1L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("User is not authorized", body.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserAverageRating_validRequest_returnsAverage() {
        ResponseEntity<Object> response = ratingController.getUserAverageRating(2L, 1L, "token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(5.0, body.get("rating"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserAverageRating_unauthorizedUser_throwsException() {
        when(authorizationService.authenticateUser(any(), any())).thenReturn(null);
        ResponseEntity<Object> response = ratingController.getUserAverageRating(2L, 1L, "token");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("User is not authorized", body.get("message"));
    }
}