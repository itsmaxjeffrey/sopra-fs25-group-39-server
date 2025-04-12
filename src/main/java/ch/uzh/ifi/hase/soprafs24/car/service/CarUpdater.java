package ch.uzh.ifi.hase.soprafs24.car.service;

import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.car.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.car.model.Car;
import ch.uzh.ifi.hase.soprafs24.car.repository.CarRepository;

@Component
public class CarUpdater {
    
    private final CarRepository carRepository;
    private final CarValidator carValidator;
    
    public CarUpdater(CarRepository carRepository, CarValidator carValidator) {
        this.carRepository = carRepository;
        this.carValidator = carValidator;
    }
    
    /**
     * Updates a car entity with values from DTO and saves it
     */
    public Car updateAndSave(Car existingCar, CarDTO carDTO) {
        // Apply updates conditionally
        if (carDTO.getCarModel() != null) {
            existingCar.setCarModel(carDTO.getCarModel());
        }
        if (carDTO.getLicensePlate() != null) {
            existingCar.setLicensePlate(carDTO.getLicensePlate());
        }
        if (carDTO.getCarPicturePath() != null) {
            existingCar.setCarPicturePath(carDTO.getCarPicturePath());
        }
        if (carDTO.getSpace() > 0) {
            existingCar.setSpace(carDTO.getSpace());
        }
        if (carDTO.getSupportedWeight() > 0) {
            existingCar.setSupportedWeight(carDTO.getSupportedWeight());
        }
        existingCar.setElectric(carDTO.isElectric());
        
        // Validate updated car
        carValidator.validateCar(existingCar);
        
        // Save and return
        existingCar = carRepository.save(existingCar);
        carRepository.flush();
        
        return existingCar;
    }

}