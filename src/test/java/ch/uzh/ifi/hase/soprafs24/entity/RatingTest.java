package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RatingTest {

    private Rating rating;
    private User fromUser;
    private User toUser;
    private Contract contract;

    @BeforeEach
    public void setup() {
        rating = new Rating();
        fromUser = new User();
        toUser = new User();
        contract = new Contract();
    }

    @Test
    public void testRatingId() {
        assertNull(rating.getRatingId());
        rating.setRatingId(1L);
        assertEquals(1L, rating.getRatingId());
    }

    @Test
    public void testFromUser() {
        assertNull(rating.getFromUser());
        rating.setFromUser(fromUser);
        assertEquals(fromUser, rating.getFromUser());
    }

    @Test
    public void testToUser() {
        assertNull(rating.getToUser());
        rating.setToUser(toUser);
        assertEquals(toUser, rating.getToUser());
    }

    @Test
    public void testContract() {
        assertNull(rating.getContract());
        rating.setContract(contract);
        assertEquals(contract, rating.getContract());
    }

    @Test
    public void testRatingValue() {
        assertNull(rating.getRatingValue());
        rating.setRatingValue(5);
        assertEquals(5, rating.getRatingValue());
    }

    @Test
    public void testFlagIssues() {
        assertFalse(rating.isFlagIssues());
        rating.setFlagIssues(true);
        assertTrue(rating.isFlagIssues());
    }

    @Test
    public void testComment() {
        assertNull(rating.getComment());
        rating.setComment("Great service!");
        assertEquals("Great service!", rating.getComment());
    }

    @Test
    public void testEquals() {
        Rating rating1 = new Rating();
        Rating rating2 = new Rating();
        
        // Test equals with null
        assertFalse(rating1.equals(null));
        
        // Test equals with same object
        assertTrue(rating1.equals(rating1));
        
        // Test equals with different objects but same ID
        rating1.setRatingId(1L);
        rating2.setRatingId(1L);
        assertTrue(rating1.equals(rating2));
        
        // Test equals with different IDs
        rating2.setRatingId(2L);
        assertFalse(rating1.equals(rating2));
    }

    @Test
    public void testHashCode() {
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
    public void testToString() {
        rating.setRatingId(1L);
        rating.setRatingValue(5);
        rating.setFlagIssues(false);
        rating.setComment("Great service!");
        
        String expected = "Rating(ratingId=1, fromUser=null, toUser=null, contract=null, ratingValue=5, flagIssues=false, comment=Great service!)";
        assertEquals(expected, rating.toString());
    }
} 