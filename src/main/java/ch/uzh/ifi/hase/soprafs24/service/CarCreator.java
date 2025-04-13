package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.CarDTOMapper;

@Component
public class CarCreator {
    
    private final CarRepository carRepository;
    private final CarDTOMapper carDTOMapper;
    private final CarValidator carValidator;
    
    public CarCreator(CarRepository carRepository, CarDTOMapper carDTOMapper, CarValidator carValidator) {
        this.carRepository = carRepository;
        this.carDTOMapper = carDTOMapper;
        this.carValidator = carValidator;
    }
    
    /**
     * Creates a new car entity from a DTO
     */
    public Car createCarFromDTO(CarDTO carDTO) {
        // Use the mapper to convert DTO to entity
        Car car = carDTOMapper.convertCarDTOToEntity(carDTO);
        
        // Validate and save
        return createCar(car);
    }
    
    /**
     * Validates and persists a car entity
     */
    public Car createCar(Car car) {
        // Validate car properties
        carValidator.validateCar(car);
        
        // Save car to database
        car = carRepository.save(car);
        carRepository.flush();
        
        return car;
    }
}