package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;

class AuthenticatedUserDTOTest {

    @Test
    void testAuthenticatedUserDTOFields() {
        AuthenticatedUserDTO dto = new AuthenticatedUserDTO();
        
        // Test setting and getting all fields
        dto.setToken("test-token");
        dto.setUserId(1L);
        dto.setUserAccountType(UserAccountType.DRIVER);
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPhoneNumber("+41791234567");
        dto.setWalletBalance(100.0);
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        dto.setUserBio("Test bio");
        dto.setProfilePicturePath("/path/to/picture.jpg");

        assertEquals("test-token", dto.getToken());
        assertEquals(1L, dto.getUserId());
        assertEquals(UserAccountType.DRIVER, dto.getUserAccountType());
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("+41791234567", dto.getPhoneNumber());
        assertEquals(100.0, dto.getWalletBalance());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getBirthDate());
        assertEquals("Test bio", dto.getUserBio());
        assertEquals("/path/to/picture.jpg", dto.getProfilePicturePath());
    }
} 