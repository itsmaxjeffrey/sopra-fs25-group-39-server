package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequesterTest {

    private Requester requester;
    private Contract contract;

    @BeforeEach
    void setup() {
        requester = new Requester();
        contract = new Contract();
    }

    @Test
    void testContracts() {
        // Test initial state
        assertNotNull(requester.getContracts());
        assertEquals(0, requester.getContracts().size());

        // Test adding a contract
        requester.addContract(contract);
        assertEquals(1, requester.getContracts().size());
        assertTrue(requester.getContracts().contains(contract));
        assertEquals(requester, contract.getRequester());

        // Test removing a contract
        requester.removeContract(contract);
        assertEquals(0, requester.getContracts().size());
        assertFalse(requester.getContracts().contains(contract));
        assertNull(contract.getRequester());
    }

    @Test
    void testInheritedFields() {
        // Test inherited fields from User
        Long userId = 1L;
        requester.setUserId(userId);
        assertEquals(userId, requester.getUserId());

        String username = "testrequester";
        requester.setUsername(username);
        assertEquals(username, requester.getUsername());

        String email = "requester@example.com";
        requester.setEmail(email);
        assertEquals(email, requester.getEmail());

        String firstName = "John";
        requester.setFirstName(firstName);
        assertEquals(firstName, requester.getFirstName());

        String lastName = "Doe";
        requester.setLastName(lastName);
        assertEquals(lastName, requester.getLastName());
    }

    @Test
    void testEqualsAndHashCode() {
        Requester requester1 = new Requester();
        Requester requester2 = new Requester();
        
        // Test equals with null
        assertFalse(requester1.equals(null));
        
        // Test equals with same object
        assertTrue(requester1.equals(requester1));
        
        // Test equals with different objects but same ID
        requester1.setUserId(1L);
        requester2.setUserId(1L);
        assertTrue(requester1.equals(requester2));
        
        // Test equals with different IDs
        requester2.setUserId(2L);
        assertFalse(requester1.equals(requester2));
    }

    @Test
    void testToString() {
        requester.setUserId(1L);
        requester.setUsername("testrequester");
        requester.setEmail("requester@example.com");
        requester.setFirstName("John");
        requester.setLastName("Doe");
        
        String toString = requester.toString();
        assertTrue(toString.contains("userId=1"));
        assertTrue(toString.contains("username=testrequester"));
        assertTrue(toString.contains("email=requester@example.com"));
        assertTrue(toString.contains("firstName=John"));
        assertTrue(toString.contains("lastName=Doe"));
    }
} 