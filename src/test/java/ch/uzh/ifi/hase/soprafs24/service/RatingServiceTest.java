package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.repository.RatingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ContractService contractService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingService ratingService;

    private Rating testRating;
    private Requester testRequester;
    private Driver testDriver;
    private Contract testContract;
    private RatingPostDTO testRatingPostDTO;
    private RatingPutDTO testRatingPutDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test users
        testRequester = new Requester();
        testRequester.setUserId(1L);
        testRequester.setUserAccountType(UserAccountType.REQUESTER);

        testDriver = new Driver();
        testDriver.setUserId(2L);
        testDriver.setUserAccountType(UserAccountType.DRIVER);

        // Create test contract
        testContract = new Contract();
        testContract.setContractId(1L);
        testContract.setRequester(testRequester);
        testContract.setDriver(testDriver);
        testContract.setContractStatus(ContractStatus.COMPLETED);

        // Create test rating
        testRating = new Rating();
        testRating.setRatingId(1L);
        testRating.setFromUser(testRequester);
        testRating.setToUser(testDriver);
        testRating.setContract(testContract);
        testRating.setRatingValue(5);
        testRating.setFlagIssues(false);
        testRating.setComment("Great service!");

        // Create test DTO
        testRatingPostDTO = new RatingPostDTO();
        testRatingPostDTO.setContractId(1L);
        testRatingPostDTO.setRatingValue(5);
        testRatingPostDTO.setFlagIssues(false);
        testRatingPostDTO.setComment("Great service!");

        testRatingPutDTO = new RatingPutDTO();
        testRatingPutDTO.setRatingValue(4);
        testRatingPutDTO.setFlagIssues(true);
        testRatingPutDTO.setComment("Updated comment");

        // Mock repository responses
        when(ratingRepository.findByRatingId(any())).thenReturn(testRating);
        when(ratingRepository.save(any())).thenReturn(testRating);
        when(ratingRepository.findByToUser_UserId(any())).thenReturn(Arrays.asList(testRating));
        when(ratingRepository.findByContract_ContractId(any())).thenReturn(Arrays.asList(testRating));
        when(userRepository.findById(any())).thenReturn(Optional.of(testRequester));
        when(contractService.getContractById(any())).thenReturn(testContract);
    }

    @Test
    void getRatingById_validId_returnsRating() {
        Rating rating = ratingService.getRatingById(1L);
        assertNotNull(rating);
        assertEquals(1L, rating.getRatingId());
        assertEquals(5, rating.getRatingValue());
    }

    @Test
    void getRatingById_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ratingService.getRatingById(null));
    }

    @Test
    void getRatingById_negativeId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ratingService.getRatingById(-1L));
    }

    @Test
    void createRating_validInput_createsRating() {
        Rating rating = ratingService.createRating(testRatingPostDTO, 1L);
        assertNotNull(rating);
        assertEquals(5, rating.getRatingValue());
        assertEquals(testRequester, rating.getFromUser());
        assertEquals(testDriver, rating.getToUser());
        assertEquals(testContract, rating.getContract());
    }

    @Test
    void createRating_invalidContractState_throwsException() {
        testContract.setContractStatus(ContractStatus.REQUESTED);
        assertThrows(ResponseStatusException.class, () -> ratingService.createRating(testRatingPostDTO, 1L));
    }

    @Test
    void createRating_invalidUserType_throwsException() {
        testRequester.setUserAccountType(UserAccountType.DRIVER);
        assertThrows(ResponseStatusException.class, () -> ratingService.createRating(testRatingPostDTO, 1L));
    }

    @Test
    void createRating_invalidRatingValue_throwsException() {
        testRatingPostDTO.setRatingValue(6);
        assertThrows(ResponseStatusException.class, () -> ratingService.createRating(testRatingPostDTO, 1L));
    }

    @Test
    void createRating_duplicateRating_throwsException() {
        // Mock existing rating
        when(ratingRepository.findByContract_ContractIdAndFromUser_UserId(any(), any())).thenReturn(testRating);
        
        // Attempt to create duplicate rating
        assertThrows(ResponseStatusException.class, () -> ratingService.createRating(testRatingPostDTO, 1L));
        
        // Verify the error message
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> ratingService.createRating(testRatingPostDTO, 1L));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("You have already rated this contract", exception.getReason());
    }

    @Test
    void updateRating_validInput_updatesRating() {
        Rating updatedRating = ratingService.updateRating(1L, testRatingPutDTO, 1L);
        assertNotNull(updatedRating);
        assertEquals(4, updatedRating.getRatingValue());
        assertTrue(updatedRating.isFlagIssues());
        assertEquals("Updated comment", updatedRating.getComment());
    }

    @Test
    void updateRating_unauthorizedUser_throwsException() {
        assertThrows(ResponseStatusException.class, () -> ratingService.updateRating(1L, testRatingPutDTO, 2L));
    }

    @Test
    void deleteRating_validInput_deletesRating() {
        ratingService.deleteRating(1L, 1L);
        verify(ratingRepository, times(1)).delete(testRating);
        // Verify contract status was updated
        verify(contractService, times(1)).updateContractStatus(1L, ContractStatus.COMPLETED);
    }

    @Test
    void deleteRating_unauthorizedUser_throwsException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> ratingService.deleteRating(1L, 2L));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You can only delete your own ratings", exception.getReason());
        // Verify delete was not called
        verify(ratingRepository, never()).delete(any());
        // Verify contract status was not updated
        verify(contractService, never()).updateContractStatus(any(), any());
    }

    @Test
    void deleteRating_contractUpdateFails_throwsExceptionAndDoesNotDelete() {
        // Mock contractService to throw an exception when updating status
        doThrow(new RuntimeException("Database error")).when(contractService).updateContractStatus(1L, ContractStatus.COMPLETED);

        // Assert that the correct exception is thrown
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> ratingService.deleteRating(1L, 1L));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertTrue(exception.getReason().contains("Failed to update contract status during rating deletion"));

        // Verify that ratingRepository.delete was NOT called
        verify(ratingRepository, never()).delete(any(Rating.class));
    }

    @Test
    void getRatingsByUserId_validId_returnsRatings() {
        List<Rating> ratings = ratingService.getRatingsByUserId(2L);
        assertNotNull(ratings);
        assertEquals(1, ratings.size());
        assertEquals(testRating, ratings.get(0));
    }

    @Test
    void getRatingsByUserId_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ratingService.getRatingsByUserId(null));
    }

    @Test
    void getRatingsByContractId_validId_returnsRatings() {
        List<Rating> ratings = ratingService.getRatingsByContractId(1L);
        assertNotNull(ratings);
        assertEquals(1, ratings.size());
        assertEquals(testRating, ratings.get(0));
    }

    @Test
    void getRatingsByContractId_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ratingService.getRatingsByContractId(null));
    }

    @Test
    void getAverageRating_validId_returnsAverage() {
        Double average = ratingService.getAverageRating(2L);
        assertNotNull(average);
        assertEquals(5.0, average);
    }

    @Test
    void getAverageRating_noRatings_returnsNull() {
        when(ratingRepository.findByToUser_UserId(any())).thenReturn(Arrays.asList());
        Double average = ratingService.getAverageRating(2L);
        assertNull(average);
    }

    @Test
    void getAverageRating_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ratingService.getAverageRating(null));
    }
}