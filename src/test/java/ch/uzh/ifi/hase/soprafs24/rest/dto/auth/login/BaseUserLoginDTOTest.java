package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.login;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BaseUserLoginDTOTest {

    @Test
    void testBaseUserLoginDTOFields() {
        BaseUserLoginDTO dto = new BaseUserLoginDTO();
        
        // Test setting and getting all fields
        dto.setUsername("testuser");
        dto.setPassword("password123");

        assertEquals("testuser", dto.getUsername());
        assertEquals("password123", dto.getPassword());
    }
} 