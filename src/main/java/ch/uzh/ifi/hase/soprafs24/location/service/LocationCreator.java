package ch.uzh.ifi.hase.soprafs24.location.service;

import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.location.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.location.mapper.LocationDTOMapper;
import ch.uzh.ifi.hase.soprafs24.location.model.Location;
import ch.uzh.ifi.hase.soprafs24.location.repository.LocationRepository;


@Component
public class LocationCreator {

    
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
        
        // Save location to database
        location = locationRepository.save(location);
        locationRepository.flush();
        
        return location;
    }
}