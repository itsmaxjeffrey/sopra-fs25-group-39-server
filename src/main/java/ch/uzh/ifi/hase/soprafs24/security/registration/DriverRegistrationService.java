package ch.uzh.ifi.hase.soprafs24.security.registration;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register.DriverRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.service.CarService;
import ch.uzh.ifi.hase.soprafs24.service.FileStorageService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;


@Service
public class DriverRegistrationService {

    private final CarService carService;
    private final LocationService locationService;



    public DriverRegistrationService(
        UserRepository userRepository, 
        CarRepository carRepository,
        FileStorageService fileStorageService, 
        CarService carService, 
        LocationService locationService) {
        this.carService = carService;
        this.locationService = locationService;
    }

    public Driver registerDriver(
        DriverRegisterDTO driverRegisterDTO,
        CarDTO carDTO,
        LocationDTO locationDTO){ 
            Driver driver = new Driver();
          
            driver.setDriverLicensePath(driverRegisterDTO.getDriverLicensePath());
            driver.setDriverInsurancePath(driverRegisterDTO.getDriverInsurancePath());
            driver.setPreferredRange(driverRegisterDTO.getPreferredRange());

            //handle carDTO and save it
            if (carDTO!=null){
                Car driverCar = new Car();
                driverCar.setCarModel(carDTO.getCarModel());
                driverCar.setSpace(carDTO.getSpace());
                driverCar.setSupportedWeight(carDTO.getSupportedWeight());
                driverCar.setElectric(carDTO.isElectric());
                driverCar.setLicensePlate(carDTO.getLicensePlate());
                driverCar.setCarPicturePath(carDTO.getCarPicturePath());
                Car savedCar = carService.createCar(driverCar);
                driver.setCar(savedCar);
            }

            //handle locationDTO and save it
            if (locationDTO!=null){
                Location driverLocation = new Location();
                driverLocation.setLatitude(locationDTO.getLatitude());
                driverLocation.setLongitude(locationDTO.getLongitude());
                driverLocation.setFormattedAddress(locationDTO.getFormattedAddress());
                Location savedLocation = locationService.createLocation(driverLocation);
                driver.setLocation(savedLocation);
            }

            return driver;
    }

    
    
}
