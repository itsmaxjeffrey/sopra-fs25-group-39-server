package ch.uzh.ifi.hase.soprafs24.car.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.car.model.Car;
import ch.uzh.ifi.hase.soprafs24.car.repository.CarRepository;

@Component
public class CarValidator {
    
    private final CarRepository carRepository;
    
    public CarValidator(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
    
    /**
     * Validates that the license plate is unique
     */
    public void validateLicensePlate(Car car) {
        if (car.getLicensePlate() != null && !car.getLicensePlate().isBlank()) {
            Car existingCar = carRepository.findByLicensePlate(car.getLicensePlate());
            if (existingCar != null && !existingCar.getCarId().equals(car.getCarId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "A car with this license plate already exists");
            }
        }
    }
    
    /**
     * Validates all car properties
     * (You can add more validation methods as needed)
     */
    public void validateCar(Car car) {
        validateLicensePlate(car);
        // Add more validations as needed
    }
}