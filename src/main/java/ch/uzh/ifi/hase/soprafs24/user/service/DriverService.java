package ch.uzh.ifi.hase.soprafs24.user.service;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.car.model.Car;
import ch.uzh.ifi.hase.soprafs24.car.service.CarService;
import ch.uzh.ifi.hase.soprafs24.location.model.Location;
import ch.uzh.ifi.hase.soprafs24.location.service.LocationService;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.DriverUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.model.Driver;
import ch.uzh.ifi.hase.soprafs24.user.repository.UserRepository;

/**
 * Service for driver-specific operations
 */
@Service
public class DriverService extends AbstractUserService {
    private final CarService carService;
    private final LocationService locationService;
    
    public DriverService(
            UserRepository userRepository,
            AuthorizationService authorizationService,
            CarService carService,
            LocationService locationService) {
        super(userRepository, authorizationService);
        this.carService = carService;
        this.locationService = locationService;
    }
    
    /**
     * Updates a driver with the provided update DTO
     */
    public Driver updateDriverDetails(Driver driver, DriverUpdateDTO updates) {
        // Update common fields first
        updateCommonFields(driver, updates);
        
        // Update driver-specific fields
        updateDriverSpecificFields(driver, updates);
        
        // Update car if provided
        updateDriverCar(driver, updates);
        
        // Update location if provided
        updateDriverLocation(driver, updates);
        
        return driver;
    }
    
    /**
     * Updates driver-specific fields
     */
    private void updateDriverSpecificFields(Driver driver, DriverUpdateDTO updates) {
        if (updates.getDriverLicensePath() != null) {
            driver.setDriverLicensePath(updates.getDriverLicensePath());
        }
        
        if (updates.getDriverInsurancePath() != null) {
            driver.setDriverInsurancePath(updates.getDriverInsurancePath());
        }
        
        if (updates.getPreferredRange() > 0) {
            driver.setPreferredRange(updates.getPreferredRange());
        }
    }
    
    /**
     * Updates driver's car information
     */
    private void updateDriverCar(Driver driver, DriverUpdateDTO updates) {
        if (updates.getCar() != null) {
            // Use carService.updateCarFromDTO which handles both creation and updating
            Car updatedCar = carService.updateCarFromDTO(driver.getCar(), updates.getCar());
            driver.setCar(updatedCar);
        }
    }
    
    /**
     * Updates driver's location information
     */
    private void updateDriverLocation(Driver driver, DriverUpdateDTO updates) {
        if (updates.getLocation() != null) {
            // Use locationService.updateLocationFromDTO which handles both creation and updating
            Location updatedLocation = locationService.updateLocationFromDTO(driver.getLocation(), updates.getLocation());
            driver.setLocation(updatedLocation);
        }
    }

}