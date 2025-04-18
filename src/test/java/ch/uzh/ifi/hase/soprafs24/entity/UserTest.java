package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;

class UserTest {

    private User user;
    private Rating ratingGiven;
    private Rating ratingReceived;

    @BeforeEach
    void setup() {
        user = new User();
        ratingGiven = new Rating();
        ratingReceived = new Rating();
    }

    @Test
    void testUserId() {
        assertNull(user.getUserId());
        
        Long userId = 1L;
        user.setUserId(userId);
        assertEquals(userId, user.getUserId());
    }

    @Test
    void testUsername() {
        assertNull(user.getUsername());
        
        String username = "testuser";
        user.setUsername(username);
        assertEquals(username, user.getUsername());
    }

    @Test
    void testPassword() {
        assertNull(user.getPassword());
        
        String password = "password123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @Test
    void testEmail() {
        assertNull(user.getEmail());
        
        String email = "test@example.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    @Test
    void testUserAccountType() {
        assertNull(user.getUserAccountType());
        
        user.setUserAccountType(UserAccountType.DRIVER);
        assertEquals(UserAccountType.DRIVER, user.getUserAccountType());
    }

    @Test
    void testCreationDate() {
        assertNull(user.getCreationDate());
        
        LocalDateTime now = LocalDateTime.now();
        user.setCreationDate(now);
        assertEquals(now, user.getCreationDate());
    }

    @Test
    void testBirthDate() {
        assertNull(user.getBirthDate());
        
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        user.setBirthDate(birthDate);
        assertEquals(birthDate, user.getBirthDate());
    }

    @Test
    void testProfilePicturePath() {
        assertNull(user.getProfilePicturePath());
        
        String path = "/images/profile.jpg";
        user.setProfilePicturePath(path);
        assertEquals(path, user.getProfilePicturePath());
    }

    @Test
    void testWalletBalance() {
        assertEquals(0.0, user.getWalletBalance());
        
        Double balance = 100.0;
        user.setWalletBalance(balance);
        assertEquals(balance, user.getWalletBalance());
    }

    @Test
    void testFirstName() {
        assertNull(user.getFirstName());
        
        String firstName = "John";
        user.setFirstName(firstName);
        assertEquals(firstName, user.getFirstName());
    }

    @Test
    void testLastName() {
        assertNull(user.getLastName());
        
        String lastName = "Doe";
        user.setLastName(lastName);
        assertEquals(lastName, user.getLastName());
    }

    @Test
    void testPhoneNumber() {
        assertNull(user.getPhoneNumber());
        
        String phoneNumber = "+41791234567";
        user.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, user.getPhoneNumber());
    }

    @Test
    void testUserBio() {
        assertNull(user.getUserBio());
        
        String bio = "This is a test bio";
        user.setUserBio(bio);
        assertEquals(bio, user.getUserBio());
    }

    @Test
    void testToken() {
        assertNull(user.getToken());
        
        String token = "test-token";
        user.setToken(token);
        assertEquals(token, user.getToken());
    }

    @Test
    void testRatingsGiven() {
        assertNotNull(user.getRatingsGiven());
        assertEquals(0, user.getRatingsGiven().size());
        
        user.addRatingGiven(ratingGiven);
        assertEquals(1, user.getRatingsGiven().size());
        assertTrue(user.getRatingsGiven().contains(ratingGiven));
        assertEquals(user, ratingGiven.getFromUser());
    }

    @Test
    void testRatingsReceived() {
        assertNotNull(user.getRatingsReceived());
        assertEquals(0, user.getRatingsReceived().size());
        
        user.addRatingReceived(ratingReceived);
        assertEquals(1, user.getRatingsReceived().size());
        assertTrue(user.getRatingsReceived().contains(ratingReceived));
        assertEquals(user, ratingReceived.getToUser());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        User user2 = new User();
        
        // Test equals with null
        assertFalse(user1.equals(null));
        
        // Test equals with same object
        assertTrue(user1.equals(user1));
        
        // Test equals with different objects but same ID
        user1.setUserId(1L);
        user2.setUserId(1L);
        assertTrue(user1.equals(user2));
        
        // Test equals with different IDs
        user2.setUserId(2L);
        assertFalse(user1.equals(user2));
    }

    @Test
    void testToString() {
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        
        String toString = user.toString();
        assertTrue(toString.contains("userId=1"));
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("firstName=John"));
        assertTrue(toString.contains("lastName=Doe"));
    }
} 