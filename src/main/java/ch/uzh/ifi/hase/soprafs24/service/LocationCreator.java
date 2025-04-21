package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.LocationRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.LocationDTOMapper;


@Component
public class LocationCreator {

    private static final Logger log = LoggerFactory.getLogger(LocationCreator.class); // Add logger
    
    private final LocationRepository locationRepository;
    private final LocationDTOMapper locationDTOMapper;
    private final LocationValidator locationValidator;
    
    public LocationCreator(LocationRepository locationRepository, LocationDTOMapper locationDTOMapper, LocationValidator locationValidator) {
        this.locationRepository = locationRepository;
        this.locationDTOMapper = locationDTOMapper;
        this.locationValidator = locationValidator;
    }
    
    /**
     * Creates a new location entity from a DTO
     */
    public Location createLocationFromDTO(LocationDTO locationDTO) {
        // Use the mapper to convert DTO to entity
        Location location = locationDTOMapper.convertLocationDTOToEntity(locationDTO);
        
        
        // Validate and save
        return createLocation(location);
    }
    
    /**
     * Validates and persists a location entity
     */
    public Location createLocation(Location location) {
        // Validate location properties
        locationValidator.validateLocation(location);
        
        // *** Log before saving ***
        log.info("Attempting to save Location entity: Latitude={}, Longitude={}, FormattedAddress='{}'", 
                 location.getLatitude(), location.getLongitude(), location.getFormattedAddress());

        // Save location to database
        location = locationRepository.save(location);
        locationRepository.flush();
        
        log.info("Saved Location entity with ID: {}", location.getId());
        return location;
    }
}