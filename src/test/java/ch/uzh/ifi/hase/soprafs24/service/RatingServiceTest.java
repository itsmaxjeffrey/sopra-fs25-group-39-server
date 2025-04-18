package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.entity.User;
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

import java.util.ArrayList;
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
    }

    @Test
    void getRatingById_validId_returnsRating() {
        // given
        when(ratingRepository.findByRatingId(1L)).thenReturn(testRating);

        // when
        Rating foundRating = ratingService.getRatingById(1L);

        // then
        assertNotNull(foundRating);
        assertEquals(testRating.getRatingId(), foundRating.getRatingId());
        verify(ratingRepository, times(1)).findByRatingId(1L);
    }

    @Test
    void getRatingById_nullId_throwsException() {
        // when/then
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.getRatingById(null);
        });
        verify(ratingRepository, never()).findByRatingId(any());
    }

    @Test
    void getRatingById_negativeId_throwsException() {
        // when/then
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.getRatingById(-1L);
        });
        verify(ratingRepository, never()).findByRatingId(any());
    }

    @Test
    void createRating_validInput_createsRating() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testRequester));
        when(contractService.getContractById(1L)).thenReturn(testContract);
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);

        // when
        Rating createdRating = ratingService.createRating(testRatingPostDTO, 1L);

        // then
        assertNotNull(createdRating);
        assertEquals(testRating.getRatingId(), createdRating.getRatingId());
        assertEquals(testRating.getRatingValue(), createdRating.getRatingValue());
        verify(contractService, times(1)).updateContract(eq(1L), any(Contract.class));
    }

    @Test
    void createRating_nonRequester_throwsException() {
        // given
        testRequester.setUserAccountType(UserAccountType.DRIVER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testRequester));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.createRating(testRatingPostDTO, 1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Only requesters can create ratings", exception.getReason());
    }

    @Test
    void createRating_invalidContractState_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.REQUESTED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testRequester));
        when(contractService.getContractById(1L)).thenReturn(testContract);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.createRating(testRatingPostDTO, 1L);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Can only rate completed contracts", exception.getReason());
    }

    @Test
    void createRating_notContractOwner_throwsException() {
        // given
        User otherRequester = new User();
        otherRequester.setUserId(3L);
        otherRequester.setUserAccountType(UserAccountType.REQUESTER);
        when(userRepository.findById(3L)).thenReturn(Optional.of(otherRequester));
        when(contractService.getContractById(1L)).thenReturn(testContract);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.createRating(testRatingPostDTO, 3L);
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You can only rate your own contracts", exception.getReason());
    }

    @Test
    void createRating_invalidRatingValue_throwsException() {
        // given
        testRatingPostDTO.setRatingValue(6);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testRequester));
        when(contractService.getContractById(1L)).thenReturn(testContract);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.createRating(testRatingPostDTO, 1L);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Rating value must be between 1 and 5", exception.getReason());
    }

    @Test
    void getRatingsByUserId_validId_returnsRatings() {
        // given
        List<Rating> ratings = new ArrayList<>();
        ratings.add(testRating);
        when(ratingRepository.findByToUser_UserId(2L)).thenReturn(ratings);

        // when
        List<Rating> foundRatings = ratingService.getRatingsByUserId(2L);

        // then
        assertNotNull(foundRatings);
        assertEquals(1, foundRatings.size());
        assertEquals(testRating.getRatingId(), foundRatings.get(0).getRatingId());
        verify(ratingRepository, times(1)).findByToUser_UserId(2L);
    }

    @Test
    void getRatingsByUserId_nullId_throwsException() {
        // when/then
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.getRatingsByUserId(null);
        });
        verify(ratingRepository, never()).findByToUser_UserId(any());
    }

    @Test
    void getRatingsByContractId_validId_returnsRatings() {
        // given
        List<Rating> ratings = new ArrayList<>();
        ratings.add(testRating);
        when(ratingRepository.findByContract_ContractId(1L)).thenReturn(ratings);

        // when
        List<Rating> foundRatings = ratingService.getRatingsByContractId(1L);

        // then
        assertNotNull(foundRatings);
        assertEquals(1, foundRatings.size());
        assertEquals(testRating.getRatingId(), foundRatings.get(0).getRatingId());
        verify(ratingRepository, times(1)).findByContract_ContractId(1L);
    }

    @Test
    void getRatingsByContractId_nullId_throwsException() {
        // when/then
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.getRatingsByContractId(null);
        });
        verify(ratingRepository, never()).findByContract_ContractId(any());
    }

    @Test
    void updateRating_validInput_updatesRating() {
        // given
        RatingPutDTO updateDTO = new RatingPutDTO();
        updateDTO.setRatingValue(4);
        updateDTO.setFlagIssues(true);
        updateDTO.setComment("Updated comment");

        when(ratingRepository.findByRatingId(1L)).thenReturn(testRating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);

        // when
        Rating updatedRating = ratingService.updateRating(1L, updateDTO, 1L);

        // then
        assertNotNull(updatedRating);
        assertEquals(4, updatedRating.getRatingValue());
        assertTrue(updatedRating.isFlagIssues());
        assertEquals("Updated comment", updatedRating.getComment());
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void updateRating_nonExistentRating_throwsException() {
        // given
        RatingPutDTO updateDTO = new RatingPutDTO();
        when(ratingRepository.findByRatingId(1L)).thenReturn(null);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.updateRating(1L, updateDTO, 1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Rating not found", exception.getReason());
    }

    @Test
    void updateRating_unauthorizedUser_throwsException() {
        // given
        RatingPutDTO updateDTO = new RatingPutDTO();
        when(ratingRepository.findByRatingId(1L)).thenReturn(testRating);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.updateRating(1L, updateDTO, 2L); // Different user ID
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You can only update your own ratings", exception.getReason());
    }

    @Test
    void updateRating_invalidRatingValue_throwsException() {
        // given
        RatingPutDTO updateDTO = new RatingPutDTO();
        updateDTO.setRatingValue(6); // Invalid value
        when(ratingRepository.findByRatingId(1L)).thenReturn(testRating);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.updateRating(1L, updateDTO, 1L);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Rating value must be between 1 and 5", exception.getReason());
    }

    @Test
    void deleteRating_validInput_deletesRating() {
        // given
        Contract testContract = new Contract();
        testContract.setContractId(1L);
        testContract.setContractStatus(ContractStatus.FINALIZED);
        testRating.setContract(testContract);

        when(ratingRepository.findByRatingId(1L)).thenReturn(testRating);
        doNothing().when(ratingRepository).delete(any(Rating.class));
        when(contractService.updateContract(anyLong(), any(Contract.class))).thenReturn(testContract);

        // when
        ratingService.deleteRating(1L, 1L);

        // then
        verify(ratingRepository, times(1)).delete(testRating);
        verify(contractService, times(1)).updateContract(eq(1L), argThat(contract -> 
            contract.getContractStatus() == ContractStatus.COMPLETED));
    }

    @Test
    void deleteRating_nonExistentRating_throwsException() {
        // given
        when(ratingRepository.findByRatingId(1L)).thenReturn(null);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.deleteRating(1L, 1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Rating not found", exception.getReason());
    }

    @Test
    void deleteRating_unauthorizedUser_throwsException() {
        // given
        when(ratingRepository.findByRatingId(1L)).thenReturn(testRating);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ratingService.deleteRating(1L, 2L); // Different user ID
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You can only delete your own ratings", exception.getReason());
    }
} 