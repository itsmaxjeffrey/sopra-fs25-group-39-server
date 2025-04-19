package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.uzh.ifi.hase.soprafs24.exceptions.GoogleMapsException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class GoogleMapsService {

    private static final Logger log = LoggerFactory.getLogger(GoogleMapsService.class);
    private final String apiKey;
    private final RestTemplate restTemplate;
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public GoogleMapsService(@Value("${google.maps.api.key}") String apiKey, RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
        log.info("GoogleMapsService initialized with API key: {}", apiKey != null ? "***" : "null");
    }

    public String getApiKey() {
        return apiKey;
    }

    /**
     * Calculate the distance between two points using Google Maps Distance Matrix API
     * 
     * @param originLat Origin latitude
     * @param originLng Origin longitude
     * @param destLat Destination latitude
     * @param destLng Destination longitude
     * @return Distance in kilometers
     */
    @SuppressWarnings("unchecked")
    public double calculateDistance(double originLat, double originLng, double destLat, double destLng) {
        log.debug("Calculating distance from ({}, {}) to ({}, {})", originLat, originLng, destLat, destLng);
        
        String origins = originLat + "," + originLng;
        String destinations = destLat + "," + destLng;

        Map<String, String> params = new HashMap<>();
        params.put("origins", origins);
        params.put("destinations", destinations);
        params.put("key", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            DISTANCE_MATRIX_URL + "?origins={origins}&destinations={destinations}&key={key}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {},
            params
        );

        Map<String, Object> responseBody = response.getBody();
        log.debug("Distance Matrix API response: {}", responseBody);
        
        if (responseBody == null) {
            log.error("Distance Matrix API returned null response body");
            throw new GoogleMapsException("Failed to calculate distance: null response");
        }

        if (responseBody.containsKey("error_message")) {
            String errorMessage = (String) responseBody.get("error_message");
            log.error("Distance Matrix API error: {}", errorMessage);
            throw new GoogleMapsException("Failed to calculate distance: " + errorMessage);
        }

        if (!responseBody.containsKey("rows")) {
            log.error("Distance Matrix API response missing 'rows' field: {}", responseBody);
            throw new GoogleMapsException("Failed to calculate distance: invalid response format");
        }

        Object rowsObj = responseBody.get("rows");
        if (rowsObj == null) {
            log.error("Distance Matrix API response 'rows' is null");
            throw new GoogleMapsException("Failed to calculate distance: null rows");
        }

        Object[] rows = rowsObj instanceof List ? ((List<?>)rowsObj).toArray() : (Object[])rowsObj;
        if (rows.length == 0) {
            log.error("Distance Matrix API response 'rows' is empty");
            throw new GoogleMapsException("Failed to calculate distance: empty rows");
        }

        Map<String, Object> row = (Map<String, Object>)rows[0];
        if (!row.containsKey("elements")) {
            log.error("Distance Matrix API response missing 'elements' field: {}", row);
            throw new GoogleMapsException("Failed to calculate distance: invalid row format");
        }

        Object elementsObj = row.get("elements");
        if (elementsObj == null) {
            log.error("Distance Matrix API response 'elements' is null");
            throw new GoogleMapsException("Failed to calculate distance: null elements");
        }

        Object[] elements = elementsObj instanceof List ? ((List<?>)elementsObj).toArray() : (Object[])elementsObj;
        if (elements.length == 0) {
            log.error("Distance Matrix API response 'elements' is empty");
            throw new GoogleMapsException("Failed to calculate distance: empty elements");
        }

        Map<String, Object> element = (Map<String, Object>)elements[0];
        if (!element.containsKey("distance")) {
            log.error("Distance Matrix API response missing 'distance' field: {}", element);
            throw new GoogleMapsException("Failed to calculate distance: invalid element format");
        }

        Map<String, Object> distance = (Map<String, Object>)element.get("distance");
        if (!distance.containsKey("value")) {
            log.error("Distance Matrix API response missing 'value' field in distance: {}", distance);
            throw new GoogleMapsException("Failed to calculate distance: invalid distance format");
        }

        double result = ((Number)distance.get("value")).doubleValue() / 1000.0;
        log.debug("Calculated distance: {} km", result);
        return result;
    }

    /**
     * Get coordinates for an address using Google Maps Geocoding API
     * 
     * @param address The address to geocode
     * @return Array containing latitude and longitude
     */
    @SuppressWarnings("unchecked")
    public double[] geocodeAddress(String address) {
        log.debug("Geocoding address: {}", address);
        
        Map<String, String> params = new HashMap<>();
        params.put("address", address);
        params.put("key", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            GEOCODING_URL + "?address={address}&key={key}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {},
            params
        );

        Map<String, Object> responseBody = response.getBody();
        log.debug("Geocoding API response: {}", responseBody);
        
        if (responseBody == null) {
            log.error("Geocoding API returned null response body");
            throw new GoogleMapsException("Failed to geocode address: null response");
        }

        if (responseBody.containsKey("error_message")) {
            String errorMessage = (String) responseBody.get("error_message");
            log.error("Geocoding API error: {}", errorMessage);
            throw new GoogleMapsException("Failed to geocode address: " + errorMessage);
        }

        if (!responseBody.containsKey("results")) {
            log.error("Geocoding API response missing 'results' field: {}", responseBody);
            throw new GoogleMapsException("Failed to geocode address: invalid response format");
        }

        Object resultsObj = responseBody.get("results");
        if (resultsObj == null) {
            log.error("Geocoding API response 'results' is null");
            throw new GoogleMapsException("Failed to geocode address: null results");
        }

        Object[] results = resultsObj instanceof List ? ((List<?>)resultsObj).toArray() : (Object[])resultsObj;
        if (results.length == 0) {
            log.error("Geocoding API response 'results' is empty");
            throw new GoogleMapsException("Failed to geocode address: empty results");
        }

        Map<String, Object> result = (Map<String, Object>)results[0];
        if (!result.containsKey("geometry")) {
            log.error("Geocoding API response missing 'geometry' field: {}", result);
            throw new GoogleMapsException("Failed to geocode address: invalid result format");
        }

        Map<String, Object> geometry = (Map<String, Object>)result.get("geometry");
        if (!geometry.containsKey("location")) {
            log.error("Geocoding API response missing 'location' field: {}", geometry);
            throw new GoogleMapsException("Failed to geocode address: invalid geometry format");
        }

        Map<String, Object> location = (Map<String, Object>)geometry.get("location");
        if (!location.containsKey("lat") || !location.containsKey("lng")) {
            log.error("Geocoding API response missing 'lat' or 'lng' field: {}", location);
            throw new GoogleMapsException("Failed to geocode address: invalid location format");
        }

        double[] coordinates = new double[] {
            ((Number)location.get("lat")).doubleValue(),
            ((Number)location.get("lng")).doubleValue()
        };
        log.debug("Geocoded coordinates: {}, {}", coordinates[0], coordinates[1]);
        return coordinates;
    }
} 