package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class GoogleMapsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GoogleMapsService googleMapsService;

    private final String apiKey = "test-api-key";
    private final ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<Map<String, Object>>() {};

    @BeforeEach
    void setup() {
        googleMapsService = new GoogleMapsService(apiKey, restTemplate);
    }

    @Test
    void testCalculateDistance_Success() {
        // Prepare mock response
        Map<String, Object> distance = new HashMap<>();
        distance.put("value", 5000); // 5km in meters

        Map<String, Object> element = new HashMap<>();
        element.put("distance", distance);

        Map<String, Object> row = new HashMap<>();
        row.put("elements", new Object[]{element});

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("rows", new Object[]{row});

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);

        // Mock the RestTemplate call with specific URL and parameters
        doReturn(responseEntity).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            eq(responseType),
            any(Map.class)
        );

        // Test the distance calculation
        double result = googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        assertEquals(5.0, result, 0.001); // 5km
    }

    @Test
    void testCalculateDistance_ErrorResponse() {
        // Prepare error response
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error_message", "Invalid request");

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);

        // Mock the RestTemplate call with specific URL and parameters
        doReturn(responseEntity).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            eq(responseType),
            any(Map.class)
        );

        // Test that an exception is thrown
        assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
    }

    @Test
    void testGeocodeAddress_Success() {
        // Prepare mock response
        Map<String, Object> location = new HashMap<>();
        location.put("lat", 47.3769);
        location.put("lng", 8.5417);

        Map<String, Object> geometry = new HashMap<>();
        geometry.put("location", location);

        Map<String, Object> result = new HashMap<>();
        result.put("geometry", geometry);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("results", new Object[]{result});

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);

        // Mock the RestTemplate call with specific URL and parameters
        doReturn(responseEntity).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            eq(responseType),
            any(Map.class)
        );

        // Test the geocoding
        double[] coordinates = googleMapsService.geocodeAddress("Zurich, Switzerland");
        assertEquals(47.3769, coordinates[0], 0.0001);
        assertEquals(8.5417, coordinates[1], 0.0001);
    }

    @Test
    void testGeocodeAddress_ErrorResponse() {
        // Prepare error response
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error_message", "Invalid request");

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);

        // Mock the RestTemplate call with specific URL and parameters
        doReturn(responseEntity).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            eq(responseType),
            any(Map.class)
        );

        // Test that an exception is thrown
        assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Invalid Address");
        });
    }

    @Test
    void testGeocodeAddress_NoResults() {
        // Prepare empty results response
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("results", new Object[]{});

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);

        // Mock the RestTemplate call with specific URL and parameters
        doReturn(responseEntity).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            eq(responseType),
            any(Map.class)
        );

        // Test that an exception is thrown
        assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Nonexistent Place");
        });
    }

    @Test
    void testCalculateDistance_InvalidResponseFormat() {
        // Prepare invalid response format
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("invalid", "format");

        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);

        // Mock the RestTemplate call with specific URL and parameters
        doReturn(responseEntity).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            eq(responseType),
            any(Map.class)
        );

        // Test that an exception is thrown
        assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
    }
} 