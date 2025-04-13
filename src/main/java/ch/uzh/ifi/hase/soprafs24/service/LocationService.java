package ch.uzh.ifi.hase.soprafs24.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.LocationRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;

@Service
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationCreator locationCreator;
    private final LocationUpdater locationUpdater;


    private final Logger log = LoggerFactory.getLogger(LocationService.class);

    public LocationService(
        LocationCreator locationCreator, 
        LocationRepository locationRepository,
        LocationUpdater locationUpdater) {
            this.locationCreator = locationCreator;
            this.locationRepository = locationRepository;
            this.locationUpdater = locationUpdater;
    }

    public Location createLocation(Location location) {
        Location createdLocation = locationCreator.createLocation(location);
        log.debug("Created Location: {}", createdLocation);
        return createdLocation;
    }
    

    public Location createLocationFromDTO(LocationDTO locationDTO) {
        Location createdLocation = locationCreator.createLocationFromDTO(locationDTO);
        log.debug("Created Location from DTO: {}", createdLocation);
        return locationCreator.createLocationFromDTO(locationDTO);
    }
    
    /**
 * Updates an existing location or creates a new one
 */
public Location updateLocationFromDTO(Location existingLocation, LocationDTO locationDTO) {
    // If location doesn't exist, create a new one
    if (existingLocation == null) {
        return createLocationFromDTO(locationDTO);
    }
    
    // Update and save the location
    Location updatedLocation = locationUpdater.updateAndSaveLocation(existingLocation, locationDTO);
    
    log.debug("Updated Location: {}", updatedLocation);
    return updatedLocation;
}


    public Location getLocationById(Long locationId){
        return locationRepository.findById(locationId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
            "Location with ID " + locationId + " not found"));
    }
}
