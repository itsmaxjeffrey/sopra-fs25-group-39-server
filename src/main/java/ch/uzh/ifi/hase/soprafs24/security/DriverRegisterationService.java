package ch.uzh.ifi.hase.soprafs24.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DriverRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.service.CarService;
import ch.uzh.ifi.hase.soprafs24.service.FileStorageService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;


@Service
public class DriverRegisterationService {

    //logger
    private final Logger log = LoggerFactory.getLogger(DriverRegisterationService.class);
    
    
    private final FileStorageService fileStorageService;
    private final CarService carService;
    private final LocationService locationService;



    public DriverRegisterationService(
        UserRepository userRepository, 
        CarRepository carRepository,
        FileStorageService fileStorageService, 
        CarService carService, 
        LocationService locationService) {
        this.fileStorageService = fileStorageService;
        this.carService = carService;
        this.locationService = locationService;
    }

    public Driver registerDriver(
    DriverRegisterDTO driverRegisterDTO,
    CarDTO carDTO,
    LocationDTO locationDTO,
    @Nullable MultipartFile driverLicense, 
    @Nullable MultipartFile driverInsurance,
    @Nullable MultipartFile driverCarPicture){ 
        Driver driver = new Driver();
        // driver license upload
        String driverLicensePath = fileStorageService.storeFile(driverLicense, "driver-licenses");
        driver.setDriverLicensePath(driverLicensePath);

        // driver license upload 
        String driverInsurancePath = fileStorageService.storeFile(driverInsurance, "driver-insurances");
        driver.setDriverInsurancePath(driverInsurancePath);
        
        // Set driver-specific fields
        driver.setPreferredRange(driverRegisterDTO.getPreferredRange());

        //handle carDTO and save it
        Car driverCar = new Car();
        driverCar.setCarModel(carDTO.getCarModel());
        driverCar.setSpace(carDTO.getSpace());
        driverCar.setSupportedWeight(carDTO.getSupportedWeight());
        driverCar.setElectric(carDTO.isElectric());
        driverCar.setLicensePlate(carDTO.getLicensePlate());
        String carPicturePath = fileStorageService.storeFile(driverCarPicture, "car-pictures");
        driverCar.setCarPicturePath(carPicturePath);
        Car savedCar = carService.createCar(driverCar);
        driver.setCar(savedCar);

        //handle locationDTO and save it
        Location driverLocation = new Location();
        driverLocation.setLatitude(locationDTO.getLatitude());
        driverLocation.setLongitude(locationDTO.getLongitude());
        driverLocation.setFormattedAddress(locationDTO.getFormattedAddress());
        Location savedLocation = locationService.createLocation(driverLocation);
        driver.setLocation(savedLocation);


        return driver;
    }

    
}
