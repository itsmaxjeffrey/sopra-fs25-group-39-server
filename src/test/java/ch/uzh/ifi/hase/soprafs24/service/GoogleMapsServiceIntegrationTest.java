package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import ch.uzh.ifi.hase.soprafs24.common.config.GoogleMapsConfig;
import ch.uzh.ifi.hase.soprafs24.common.service.GoogleMapsService;

@SpringBootTest(classes = {
    GoogleMapsService.class,  // Only include the service you're testing
    GoogleMapsConfig.class    // Include the config if needed
})
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "google.maps.api.key=${GOOGLE_MAPS_API_KEY}"
})

public class GoogleMapsServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(GoogleMapsServiceIntegrationTest.class);

    
    @Autowired
    private GoogleMapsService googleMapsService;

    @Autowired
    private Environment environment;

    @Test
    public void testApiKeyLoaded() {
        assertNotNull(googleMapsService, "GoogleMapsService should be autowired");
        String apiKey = environment.getProperty("GOOGLE_MAPS_API_KEY");
        log.info("Environment API key loaded: {}", apiKey != null ? "***" : "null");
        assertNotNull(apiKey, "API key should not be null");
        assertFalse(apiKey.isEmpty(), "API key should not be empty");
        
        String serviceApiKey = googleMapsService.getApiKey();
        log.info("Service API key loaded: {}", serviceApiKey != null ? "***" : "null");
        assertNotNull(serviceApiKey, "Service API key should not be null");
        assertFalse(serviceApiKey.isEmpty(), "Service API key should not be empty");
    }

    @Test
    public void testDistanceCalculation() {
        log.info("Starting distance calculation test");
        
        // Test distance between two points in Zürich
        double originLat = 47.3769;
        double originLng = 8.5417;
        double destLat = 47.3770;
        double destLng = 8.5418;

        try {
            double distance = googleMapsService.calculateDistance(originLat, originLng, destLat, destLng);
            
            // Should be a small distance (less than 1km)
            assertTrue(distance > 0 && distance < 1, "Distance should be positive and less than 1km");
            log.info("Distance calculation successful: {} km", distance);
        } catch (Exception e) {
            log.error("Distance calculation failed", e);
            throw e;
        }
    }

    @Test
    public void testGeocoding() {
        log.info("Starting geocoding test");
        
        // Test geocoding of a known address in Zürich
        String address = "Rämistrasse 101, 8006 Zürich";
        
        try {
            double[] coordinates = googleMapsService.geocodeAddress(address);
            
            assertNotNull(coordinates);
            assertEquals(2, coordinates.length);
            // Zürich coordinates should be roughly around these values
            assertTrue(coordinates[0] > 47.3 && coordinates[0] < 47.4, "Latitude should be in Zürich range");
            assertTrue(coordinates[1] > 8.5 && coordinates[1] < 8.6, "Longitude should be in Zürich range");
            log.info("Geocoding successful: {}, {}", coordinates[0], coordinates[1]);
        } catch (Exception e) {
            log.error("Geocoding failed", e);
            throw e;
        }
    }
} 