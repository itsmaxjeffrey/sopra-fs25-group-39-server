package ch.uzh.ifi.hase.soprafs24.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;

@Service
@Transactional
public class CarService {
    
    private final Logger log = LoggerFactory.getLogger(CarService.class);
    
    private final CarRepository carRepository;
    
    @Autowired
    public CarService(@Qualifier("carRepository") CarRepository carRepository) {
        this.carRepository = carRepository;
    }
    
    /**
     * Creates a new car entity
     * 
     * @param car The car entity to create
     * @return The created car entity
     */
    public Car createCar(Car car) {
        // Check if car with license plate already exists
        if (car.getLicensePlate() != null && !car.getLicensePlate().isBlank()) {
            Car existingCar = carRepository.findByLicensePlate(car.getLicensePlate());
            if (existingCar != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "A car with this license plate already exists");
            }
        }
        
        // Save car to database
        car = carRepository.save(car);
        carRepository.flush();
        
        log.debug("Created Car: {}", car);
        return car;
    }
    
    /**
     * Retrieves a car by ID
     * 
     * @param carId The ID of the car to retrieve
     * @return The car entity
     */
    public Car getCarById(Long carId) {
        return carRepository.findById(carId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Car with ID " + carId + " not found"));
    }
}