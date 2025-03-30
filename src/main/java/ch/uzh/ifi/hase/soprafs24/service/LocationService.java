package ch.uzh.ifi.hase.soprafs24.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.LocationRepository;

@Service
@Transactional
public class LocationService {
    
    private final Logger log = LoggerFactory.getLogger(LocationService.class);
    
    private final LocationRepository locationRepository;
    
    @Autowired
    public LocationService(@Qualifier("locationRepository") LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }
    
    /**
     * Creates a new location entity
     * 
     * @param location The location entity to create
     * @return The created location entity
     */
    public Location createLocation(Location location) {
        // Validate location coordinates
        if (location.getLatitude() == null || location.getLongitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Location must include both latitude and longitude");
        }
        
        // Save location to database
        location = locationRepository.save(location);
        locationRepository.flush();
        
        log.debug("Created Location: {}", location);
        return location;
    }
    
    /**
     * Retrieves a location by ID
     * 
     * @param locationId The ID of the location to retrieve
     * @return The location entity
     */
    public Location getLocationById(Long locationId) {
        return locationRepository.findById(locationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Location with ID " + locationId + " not found"));
    }
}