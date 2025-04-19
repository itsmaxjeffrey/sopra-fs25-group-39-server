package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;

class AuthenticatedRequesterDTOTest {

    @Test
    void testAuthenticatedRequesterDTOFields() {
        AuthenticatedRequesterDTO dto = new AuthenticatedRequesterDTO();
        
        // Test setting and getting all fields
        dto.setToken("test-token");
        dto.setUserId(1L);
        dto.setUserAccountType(UserAccountType.REQUESTER);
        dto.setUsername("requesteruser");
        dto.setEmail("requester@example.com");
        dto.setFirstName("Requester");
        dto.setLastName("Test");
        dto.setPhoneNumber("+41791234567");
        dto.setWalletBalance(100.0);
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        dto.setUserBio("Requester bio");
        dto.setProfilePicturePath("/path/to/picture.jpg");

        // Test all fields
        assertEquals("test-token", dto.getToken());
        assertEquals(1L, dto.getUserId());
        assertEquals(UserAccountType.REQUESTER, dto.getUserAccountType());
        assertEquals("requesteruser", dto.getUsername());
        assertEquals("requester@example.com", dto.getEmail());
        assertEquals("Requester", dto.getFirstName());
        assertEquals("Test", dto.getLastName());
        assertEquals("+41791234567", dto.getPhoneNumber());
        assertEquals(100.0, dto.getWalletBalance());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getBirthDate());
        assertEquals("Requester bio", dto.getUserBio());
        assertEquals("/path/to/picture.jpg", dto.getProfilePicturePath());
    }
} 