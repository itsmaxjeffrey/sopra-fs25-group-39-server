package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.CarDTOMapper;

@Component
public class CarUpdater {
    
    private final CarRepository carRepository;
    private final CarValidator carValidator;
    
    public CarUpdater(CarRepository carRepository, CarValidator carValidator, CarDTOMapper carDTOMapper) {
        this.carRepository = carRepository;
        this.carValidator = carValidator;
    }
    
    /**
     * Updates a car entity with values from DTO and saves it
     */
    public Car updateAndSave(Car existingCar, CarDTO carDTO) {
        if (carDTO == null) {
            throw new IllegalArgumentException("CarDTO cannot be null");
        }

        // Apply updates from DTO to the existing car entity
        // Only update fields if they are provided in the DTO

        if (carDTO.getCarModel() != null) {
            existingCar.setCarModel(carDTO.getCarModel());
        }
        if (carDTO.getLicensePlate() != null) {
            existingCar.setLicensePlate(carDTO.getLicensePlate());
        }
        if (carDTO.getCarPicturePath() != null) {
            existingCar.setCarPicturePath(carDTO.getCarPicturePath());
        }
        // Use Float object comparison to allow setting 0
        if (carDTO.getVolumeCapacity() >= 0) { // Allow 0 capacity
            existingCar.setVolumeCapacity(carDTO.getVolumeCapacity());
        }
        if (carDTO.getWeightCapacity() >= 0) { // Allow 0 capacity
            existingCar.setWeightCapacity(carDTO.getWeightCapacity());
        }
        // Update electric status regardless of value (true/false)
        // Assuming the DTO provides a boolean, not a Boolean object that could be null
        existingCar.setElectric(carDTO.isElectric());
        
        // Validate the updated existing car
        carValidator.validateCar(existingCar);
        
        // Save and return the updated existing car
        Car savedCar = carRepository.save(existingCar);
        carRepository.flush();
        
        return savedCar;
    }

}