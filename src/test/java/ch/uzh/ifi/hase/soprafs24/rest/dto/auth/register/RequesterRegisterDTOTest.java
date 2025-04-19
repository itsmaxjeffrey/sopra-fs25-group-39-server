package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;

class RequesterRegisterDTOTest {

    @Test
    void testRequesterRegisterDTOFields() {
        RequesterRegisterDTO dto = new RequesterRegisterDTO();
        
        // Test setting and getting base fields
        dto.setUsername("requesteruser");
        dto.setPassword("requesterpass");
        dto.setEmail("requester@example.com");
        dto.setFirstName("Requester");
        dto.setLastName("Test");
        dto.setPhoneNumber("+41791234567");
        dto.setUserBio("Requester bio");

        assertEquals("requesteruser", dto.getUsername());
        assertEquals("requesterpass", dto.getPassword());
        assertEquals("requester@example.com", dto.getEmail());
        assertEquals("Requester", dto.getFirstName());
        assertEquals("Test", dto.getLastName());
        assertEquals("+41791234567", dto.getPhoneNumber());
        assertEquals("Requester bio", dto.getUserBio());
    }

    @Test
    void testUserAccountType() {
        RequesterRegisterDTO dto = new RequesterRegisterDTO();
        assertEquals(UserAccountType.REQUESTER, dto.getUserAccountType());
    }
} 