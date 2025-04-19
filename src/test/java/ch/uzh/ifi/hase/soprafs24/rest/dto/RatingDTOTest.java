package ch.uzh.ifi.hase.soprafs24.rest.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RatingDTOTest {

    @Test
    void testRatingDTOFields() {
        RatingDTO dto = new RatingDTO();
        
        // Test setting and getting all fields
        dto.setRatingId(1L);
        dto.setFromUserId(2L);
        dto.setToUserId(3L);
        dto.setContractId(4L);
        dto.setRatingValue(5);
        dto.setFlagIssues(true);
        dto.setComment("Great service!");

        assertEquals(1L, dto.getRatingId());
        assertEquals(2L, dto.getFromUserId());
        assertEquals(3L, dto.getToUserId());
        assertEquals(4L, dto.getContractId());
        assertEquals(5, dto.getRatingValue());
        assertTrue(dto.isFlagIssues());
        assertEquals("Great service!", dto.getComment());
    }

    @Test
    void testFlagIssuesFalse() {
        RatingDTO dto = new RatingDTO();
        dto.setFlagIssues(false);
        assertFalse(dto.isFlagIssues());
    }

    @Test
    void testNullComment() {
        RatingDTO dto = new RatingDTO();
        dto.setComment(null);
        assertEquals(null, dto.getComment());
    }
} 