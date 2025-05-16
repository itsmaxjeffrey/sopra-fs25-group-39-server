package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RatingTest {

    private Rating rating;
    private User fromUser;
    private User toUser;
    private Contract contract;

    @BeforeEach
    void setup() {
        rating = new Rating();
        fromUser = new User();
        toUser = new User();
        contract = new Contract();
    }

    @Test
    void testRatingId() {
        assertNull(rating.getRatingId());
        rating.setRatingId(1L);
        assertEquals(1L, rating.getRatingId());
    }

    @Test
    void testFromUser() {
        assertNull(rating.getFromUser());
        rating.setFromUser(fromUser);
        assertEquals(fromUser, rating.getFromUser());
    }

    @Test
    void testToUser() {
        assertNull(rating.getToUser());
        rating.setToUser(toUser);
        assertEquals(toUser, rating.getToUser());
    }

    @Test
    void testContract() {
        assertNull(rating.getContract());
        rating.setContract(contract);
        assertEquals(contract, rating.getContract());
    }

    @Test
    void testRatingValue() {
        assertNull(rating.getRatingValue());
        rating.setRatingValue(5);
        assertEquals(5, rating.getRatingValue());
    }

    @Test
    void testFlagIssues() {
        assertFalse(rating.isFlagIssues());
        rating.setFlagIssues(true);
        assertTrue(rating.isFlagIssues());
    }

    @Test
    void testComment() {
        assertNull(rating.getComment());
        rating.setComment("Great service!");
        assertEquals("Great service!", rating.getComment());
    }

    @Test
    void testEquals() {
        Rating rating1 = new Rating();
        Rating rating2 = new Rating();
        
        // Test equals with null
        assertNotEquals(null, rating1);
        
        // Test equals with same object
        assertEquals(rating1, rating1);
        
        // Test equals with different objects but same ID
        rating1.setRatingId(1L);
        rating2.setRatingId(1L);
        assertEquals(rating1, rating2);
        
        // Test equals with different IDs
        rating2.setRatingId(2L);
        assertNotEquals(rating1, rating2);
    }

    @Test
    void testHashCode() {
        Rating rating1 = new Rating();
        Rating rating2 = new Rating();
        
        // Test hashCode with same ID
        rating1.setRatingId(1L);
        rating2.setRatingId(1L);
        assertEquals(rating1.hashCode(), rating2.hashCode());
        
        // Test hashCode with different IDs
        rating2.setRatingId(2L);
        assertNotEquals(rating1.hashCode(), rating2.hashCode());
    }

    @Test
    void testToString() {
        rating.setRatingId(1L);
        rating.setRatingValue(5);
        rating.setFlagIssues(false);
        rating.setComment("Great service!");
        
        String expected = "Rating(ratingId=1, fromUser=null, toUser=null, contract=null, ratingValue=5, flagIssues=false, comment=Great service!)";
        assertEquals(expected, rating.toString());
    }
} 