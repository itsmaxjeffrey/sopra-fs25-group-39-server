package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.LocationRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;

@Component
public class LocationUpdater {
    private final LocationValidator locationValidator;
    private final LocationRepository locationRepository;

    public LocationUpdater(
            LocationValidator locationValidator,
            LocationRepository locationRepository) {
                this.locationValidator = locationValidator;
                this.locationRepository = locationRepository;
    }
    

    public Location updateAndSaveLocation(Location existingLocation, LocationDTO locationDTO) {
        if (locationDTO.getLatitude() != null) {
            existingLocation.setLatitude(locationDTO.getLatitude());
        }
        if (locationDTO.getLongitude() != null) {
            existingLocation.setLongitude(locationDTO.getLongitude());
        }
        if (locationDTO.getFormattedAddress() != null) {
            existingLocation.setFormattedAddress(locationDTO.getFormattedAddress());
        }
        locationValidator.validateLocation(existingLocation);
        locationRepository.save(existingLocation);
        locationRepository.flush();    
        return existingLocation;
    }

}
