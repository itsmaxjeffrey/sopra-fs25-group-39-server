package ch.uzh.ifi.hase.soprafs24.service.car;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;

@Service
@Transactional
public class CarService {
    
    private final Logger log = LoggerFactory.getLogger(CarService.class);
    
    private final CarRepository carRepository;
    private final CarCreator carCreator;
    private final CarUpdater carUpdater;
    
    public CarService(CarRepository carRepository,
                      UserRepository userRepository,
                      CarCreator carCreator,
                      CarUpdater carUpdater) {
        this.carRepository = carRepository;
        this.carCreator = carCreator;
        this.carUpdater = carUpdater;
    }
    
    /**
     * Creates a new car entity
     */
    public Car createCar(Car car) {
        Car createdCar = carCreator.createCar(car);
        log.debug("Created Car: {}", createdCar);
        return createdCar;
    }
    
    /**
     * Creates a new car from DTO
     */
    public Car createCarFromDTO(CarDTO carDTO) {
        Car createdCar = carCreator.createCarFromDTO(carDTO);
        log.debug("Created Car from DTO: {}", createdCar);
        return createdCar;
    }
    
    /**
     * Updates an existing car or creates a new one
     */
    public Car updateCarFromDTO(Car existingCar, CarDTO carDTO) {
        // If car doesn't exist, create a new one
        if (existingCar == null) {
            return createCarFromDTO(carDTO);
        }
        
        // Update and save the car
        Car updatedCar = carUpdater.updateAndSave(existingCar, carDTO);
        
        log.debug("Updated Car: {}", updatedCar);
        return updatedCar;
    }
    
    /**
     * Retrieves a car by ID
     */
    public Car getCarById(Long carId) {
        return carRepository.findById(carId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Car with ID " + carId + " not found"));
    }
}