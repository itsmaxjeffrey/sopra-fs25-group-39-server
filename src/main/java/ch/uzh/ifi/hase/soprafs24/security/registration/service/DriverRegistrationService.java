package ch.uzh.ifi.hase.soprafs24.security.registration.service;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.car.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.car.model.Car;
import ch.uzh.ifi.hase.soprafs24.car.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.car.service.CarService;
import ch.uzh.ifi.hase.soprafs24.location.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.location.model.Location;
import ch.uzh.ifi.hase.soprafs24.location.service.LocationService;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.DriverRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.storage.service.FileStorageService;
import ch.uzh.ifi.hase.soprafs24.user.model.Driver;
import ch.uzh.ifi.hase.soprafs24.user.repository.UserRepository;


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
