package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleMapsService {

    private final String apiKey;
    private final RestTemplate restTemplate;
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    @Autowired
    public GoogleMapsService(String googleMapsApiKey) {
        this.apiKey = googleMapsApiKey;
        this.restTemplate = new RestTemplate();
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
    public double calculateDistance(double originLat, double originLng, double destLat, double destLng) {
        String origins = originLat + "," + originLng;
        String destinations = destLat + "," + destLng;

        Map<String, String> params = new HashMap<>();
        params.put("origins", origins);
        params.put("destinations", destinations);
        params.put("key", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.exchange(
            DISTANCE_MATRIX_URL + "?origins={origins}&destinations={destinations}&key={key}",
            HttpMethod.GET,
            null,
            Map.class,
            params
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("rows")) {
            Map<String, Object> row = ((Map<String, Object>)((Object[])responseBody.get("rows"))[0]);
            Map<String, Object> element = ((Map<String, Object>)((Object[])row.get("elements"))[0]);
            Map<String, Object> distance = (Map<String, Object>)element.get("distance");
            return ((Number)distance.get("value")).doubleValue() / 1000.0; // Convert meters to kilometers
        }

        throw new RuntimeException("Failed to calculate distance");
    }

    /**
     * Get coordinates for an address using Google Maps Geocoding API
     * 
     * @param address The address to geocode
     * @return Array containing latitude and longitude
     */
    public double[] geocodeAddress(String address) {
        Map<String, String> params = new HashMap<>();
        params.put("address", address);
        params.put("key", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.exchange(
            GEOCODING_URL + "?address={address}&key={key}",
            HttpMethod.GET,
            null,
            Map.class,
            params
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("results")) {
            Map<String, Object> result = ((Map<String, Object>)((Object[])responseBody.get("results"))[0]);
            Map<String, Object> location = (Map<String, Object>)((Map<String, Object>)result.get("geometry")).get("location");
            return new double[] {
                ((Number)location.get("lat")).doubleValue(),
                ((Number)location.get("lng")).doubleValue()
            };
        }

        throw new RuntimeException("Failed to geocode address");
    }
} 