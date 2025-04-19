package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;

class BaseUserRegisterDTOTest {

    @Test
    void testBaseUserRegisterDTOFields() {
        BaseUserRegisterDTO dto = new BaseUserRegisterDTO();
        
        // Test setting and getting all fields
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setEmail("test@example.com");
        dto.setUserAccountType(UserAccountType.DRIVER);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPhoneNumber("+41791234567");
        dto.setUserBio("Test bio");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        dto.setProfilePicturePath("/path/to/picture.jpg");

        assertEquals("testuser", dto.getUsername());
        assertEquals("password123", dto.getPassword());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals(UserAccountType.DRIVER, dto.getUserAccountType());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("+41791234567", dto.getPhoneNumber());
        assertEquals("Test bio", dto.getUserBio());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getBirthDate());
        assertEquals("/path/to/picture.jpg", dto.getProfilePicturePath());
    }

    @Test
    void testJsonTypeInfoAnnotation() {
        // Test that the JsonTypeInfo annotation is properly set up
        assertNotNull(BaseUserRegisterDTO.class.getAnnotation(com.fasterxml.jackson.annotation.JsonTypeInfo.class));
    }
} 