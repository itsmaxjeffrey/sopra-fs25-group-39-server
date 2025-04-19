package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

    @Test
    void testCalculateDistance_NullResponse() {
        // given
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(null, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: null response", exception.getMessage());
    }

    @Test
    void testCalculateDistance_ErrorInResponse() {
        // given
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error_message", "Invalid API key");
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: Invalid API key", exception.getMessage());
    }

    @Test
    void testCalculateDistance_MissingRows() {
        // given
        Map<String, Object> responseBody = new HashMap<>();
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: invalid response format", exception.getMessage());
    }

    @Test
    void testCalculateDistance_NullRows() {
        // given
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("rows", null);
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: null rows", exception.getMessage());
    }

    @Test
    void testCalculateDistance_EmptyRows() {
        // given
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("rows", new ArrayList<>());
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: empty rows", exception.getMessage());
    }

    @Test
    void testCalculateDistance_MissingElements() {
        // given
        Map<String, Object> row = new HashMap<>();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("rows", Collections.singletonList(row));
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: invalid row format", exception.getMessage());
    }

    @Test
    void testCalculateDistance_NullElements() {
        // given
        Map<String, Object> row = new HashMap<>();
        row.put("elements", null);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("rows", Collections.singletonList(row));
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: null elements", exception.getMessage());
    }

    @Test
    void testCalculateDistance_EmptyElements() {
        // given
        Map<String, Object> row = new HashMap<>();
        row.put("elements", new ArrayList<>());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("rows", Collections.singletonList(row));
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: empty elements", exception.getMessage());
    }

    @Test
    void testCalculateDistance_MissingDistance() {
        // given
        Map<String, Object> element = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("elements", Collections.singletonList(element));
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("rows", Collections.singletonList(row));
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: invalid element format", exception.getMessage());
    }

    @Test
    void testCalculateDistance_MissingValue() {
        // given
        Map<String, Object> distance = new HashMap<>();
        Map<String, Object> element = new HashMap<>();
        element.put("distance", distance);
        Map<String, Object> row = new HashMap<>();
        row.put("elements", Collections.singletonList(element));
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("rows", Collections.singletonList(row));
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418);
        });
        assertEquals("Failed to calculate distance: invalid distance format", exception.getMessage());
    }

    @Test
    void testGeocodeAddress_NullResponse() {
        // given
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(null, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Zurich, Switzerland");
        });
        assertEquals("Failed to geocode address: null response", exception.getMessage());
    }

    @Test
    void testGeocodeAddress_ErrorInResponse() {
        // given
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error_message", "Invalid API key");
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Zurich, Switzerland");
        });
        assertEquals("Failed to geocode address: Invalid API key", exception.getMessage());
    }

    @Test
    void testGeocodeAddress_MissingResults() {
        // given
        Map<String, Object> responseBody = new HashMap<>();
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Zurich, Switzerland");
        });
        assertEquals("Failed to geocode address: invalid response format", exception.getMessage());
    }

    @Test
    void testGeocodeAddress_NullResults() {
        // given
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("results", null);
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Zurich, Switzerland");
        });
        assertEquals("Failed to geocode address: null results", exception.getMessage());
    }

    @Test
    void testGeocodeAddress_EmptyResults() {
        // given
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("results", new ArrayList<>());
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Zurich, Switzerland");
        });
        assertEquals("Failed to geocode address: empty results", exception.getMessage());
    }

    @Test
    void testGeocodeAddress_MissingGeometry() {
        // given
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("results", Collections.singletonList(result));
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Zurich, Switzerland");
        });
        assertEquals("Failed to geocode address: invalid result format", exception.getMessage());
    }

    @Test
    void testGeocodeAddress_MissingLocation() {
        // given
        Map<String, Object> geometry = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        result.put("geometry", geometry);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("results", Collections.singletonList(result));
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Zurich, Switzerland");
        });
        assertEquals("Failed to geocode address: invalid geometry format", exception.getMessage());
    }

    @Test
    void testGeocodeAddress_MissingCoordinates() {
        // given
        Map<String, Object> location = new HashMap<>();
        Map<String, Object> geometry = new HashMap<>();
        geometry.put("location", location);
        Map<String, Object> result = new HashMap<>();
        result.put("geometry", geometry);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("results", Collections.singletonList(result));
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        doReturn(response).when(restTemplate).exchange(
            eq("https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}"),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class),
            any(Map.class)
        );

        // when/then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            googleMapsService.geocodeAddress("Zurich, Switzerland");
        });
        assertEquals("Failed to geocode address: invalid location format", exception.getMessage());
    }
} 