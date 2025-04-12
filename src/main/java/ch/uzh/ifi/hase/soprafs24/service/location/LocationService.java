package ch.uzh.ifi.hase.soprafs24.service.location;

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


    private final Logger log = LoggerFactory.getLogger(LocationService.class);

    public LocationService(
        LocationCreator locationCreator, 
        LocationRepository locationRepository) {
            this.locationCreator = locationCreator;
            this.locationRepository = locationRepository;
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
    


    public Location getLocationById(Long locationId){
        return locationRepository.findById(locationId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
            "Location with ID " + locationId + " not found"));
    }
}
