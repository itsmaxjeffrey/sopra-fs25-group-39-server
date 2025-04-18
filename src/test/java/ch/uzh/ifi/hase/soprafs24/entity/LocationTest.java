package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocationTest {

    private Location location;

    @BeforeEach
    public void setup() {
        location = new Location();
    }

    @Test
    public void testId() {
        assertNull(location.getId());
        
        Long id = 1L;
        location.setId(id);
        assertEquals(id, location.getId());
    }

    @Test
    public void testFormattedAddress() {
        assertNull(location.getFormattedAddress());
        
        String address = "Bahnhofstrasse 1, 8001 Zürich";
        location.setFormattedAddress(address);
        assertEquals(address, location.getFormattedAddress());
    }

    @Test
    public void testLatitude() {
        assertNull(location.getLatitude());
        
        Double latitude = 47.3769;
        location.setLatitude(latitude);
        assertEquals(latitude, location.getLatitude());
    }

    @Test
    public void testLongitude() {
        assertNull(location.getLongitude());
        
        Double longitude = 8.5417;
        location.setLongitude(longitude);
        assertEquals(longitude, location.getLongitude());
    }

    @Test
    public void testEqualsAndHashCode() {
        Location location1 = new Location();
        Location location2 = new Location();
        
        // Test equals with null
        assertFalse(location1.equals(null));
        
        // Test equals with same object
        assertTrue(location1.equals(location1));
        
        // Test equals with different objects but same ID
        location1.setId(1L);
        location2.setId(1L);
        assertTrue(location1.equals(location2));
        
        // Test equals with different IDs
        location2.setId(2L);
        assertFalse(location1.equals(location2));
    }

    @Test
    public void testToString() {
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