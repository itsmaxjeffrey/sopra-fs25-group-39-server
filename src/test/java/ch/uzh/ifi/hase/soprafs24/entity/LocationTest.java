package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationTest {

    private Location location;

    @BeforeEach
    void setup() {
        location = new Location();
    }

    @Test
    void testId() {
        assertNull(location.getId());
        
        Long id = 1L;
        location.setId(id);
        assertEquals(id, location.getId());
    }

    @Test
    void testFormattedAddress() {
        assertNull(location.getFormattedAddress());
        
        String address = "Bahnhofstrasse 1, 8001 Zürich";
        location.setFormattedAddress(address);
        assertEquals(address, location.getFormattedAddress());
    }

    @Test
    void testLatitude() {
        assertNull(location.getLatitude());
        
        Double latitude = 47.3769;
        location.setLatitude(latitude);
        assertEquals(latitude, location.getLatitude());
    }

    @Test
    void testLongitude() {
        assertNull(location.getLongitude());
        
        Double longitude = 8.5417;
        location.setLongitude(longitude);
        assertEquals(longitude, location.getLongitude());
    }

    @Test
    void testEqualsAndHashCode() {
        Location location1 = new Location();
        Location location2 = new Location();
        
        // Test equals with null
        assertNotEquals(null, location1);
        
        // Test equals with same object
        assertEquals(location1, location1);
        
        // Test equals with different objects but same ID
        location1.setId(1L);
        location2.setId(1L);
        assertEquals(location1, location2);
        
        // Test equals with different IDs
        location2.setId(2L);
        assertNotEquals(location1, location2);
    }

    @Test
    void testToString() {
        location.setId(1L);
        location.setFormattedAddress("Bahnhofstrasse 1, 8001 Zürich");
        location.setLatitude(47.3769);
        location.setLongitude(8.5417);
        
        String toString = location.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("formattedAddress=Bahnhofstrasse 1, 8001 Zürich"));
        assertTrue(toString.contains("latitude=47.3769"));
        assertTrue(toString.contains("longitude=8.5417"));
    }
} 