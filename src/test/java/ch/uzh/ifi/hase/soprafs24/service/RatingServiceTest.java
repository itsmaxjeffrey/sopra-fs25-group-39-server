package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    private Rating testRating;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test rating
        testRating = new Rating();
        testRating.setRatingId(1L);
        testRating.setRatingValue(5);
        testRating.setFlagIssues(false);
        testRating.setComment("Great service!");
    }

    @Test
    void getRatingById_validId_returnsRating() {
        // given
        when(ratingRepository.findByRatingId(1L)).thenReturn(testRating);

        // when
        Rating found = ratingService.getRatingById(1L);

        // then
        assertNotNull(found);
        assertEquals(testRating.getRatingId(), found.getRatingId());
        assertEquals(testRating.getRatingValue(), found.getRatingValue());
        assertEquals(testRating.getComment(), found.getComment());
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
    void getRatingById_zeroId_throwsException() {
        // when/then
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.getRatingById(0L);
        });
        verify(ratingRepository, never()).findByRatingId(any());
    }

    @Test
    void getRatingById_nonExistentId_returnsNull() {
        // given
        when(ratingRepository.findByRatingId(99L)).thenReturn(null);

        // when
        Rating found = ratingService.getRatingById(99L);

        // then
        assertNull(found);
        verify(ratingRepository, times(1)).findByRatingId(99L);
    }
} 