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
    private final CarDTOMapper carDTOMapper;
    
    public CarUpdater(CarRepository carRepository, CarValidator carValidator, CarDTOMapper carDTOMapper) {
        this.carRepository = carRepository;
        this.carValidator = carValidator;
        this.carDTOMapper = carDTOMapper;
    }
    
    /**
     * Updates a car entity with values from DTO and saves it
     */
    public Car updateAndSave(Car existingCar, CarDTO carDTO) {
        if (carDTO == null) {
            throw new IllegalArgumentException("CarDTO cannot be null");
        }

        // Create a new car entity from the DTO
        Car updatedCar = carDTOMapper.convertCarDTOToEntity(carDTO);
        
        // Preserve the existing car's ID
        updatedCar.setCarId(existingCar.getCarId());
        
        // Handle partial updates by preserving existing values when DTO fields are null
        if (carDTO.getCarModel() == null) {
            updatedCar.setCarModel(existingCar.getCarModel());
        }
        if (carDTO.getLicensePlate() == null) {
            updatedCar.setLicensePlate(existingCar.getLicensePlate());
        }
        if (carDTO.getCarPicturePath() == null) {
            updatedCar.setCarPicturePath(existingCar.getCarPicturePath());
        }
        if (carDTO.getSpace() <= 0) {
            updatedCar.setSpace(existingCar.getSpace());
        }
        if (carDTO.getSupportedWeight() <= 0) {
            updatedCar.setSupportedWeight(existingCar.getSupportedWeight());
        }
        
        // Validate updated car
        carValidator.validateCar(updatedCar);
        
        // Save and return
        updatedCar = carRepository.save(updatedCar);
        carRepository.flush();
        
        return updatedCar;
    }

}