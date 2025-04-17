package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Location;

@Component
public class LocationValidator {
    
    /**
     * Validates all location properties
     * 
     * @param location The location to validate
     * @throws ResponseStatusException if validation fails
     */
    public void validateLocation(Location location) {
        validateCoordinates(location);
        validateAddress(location);
    }
    
    /**
     * Validates that latitude and longitude are present and within valid ranges
     * 
     * @param location The location to validate
     * @throws ResponseStatusException if validation fails
     */
    public void validateCoordinates(Location location) {
        // Check if coordinates are provided
        if (location.getLatitude() == null || location.getLongitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Both latitude and longitude must be provided");
        }
        
        // Check latitude range (-90 to 90)
        if (location.getLatitude() < -90 || location.getLatitude() > 90) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Latitude must be between -90 and 90 degrees");
        }
        
        // Check longitude range (-180 to 180)
        if (location.getLongitude() < -180 || location.getLongitude() > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Longitude must be between -180 and 180 degrees");
        }
    }
    
    /**
     * Validates the address field if provided
     * 
     * @param location The location to validate
     * @throws ResponseStatusException if validation fails
     */
    public void validateAddress(Location location) {
        // If formatted address is provided, it shouldn't be empty
        if (location.getFormattedAddress() != null && location.getFormattedAddress().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Formatted address cannot be empty if provided");
        }
    }
}