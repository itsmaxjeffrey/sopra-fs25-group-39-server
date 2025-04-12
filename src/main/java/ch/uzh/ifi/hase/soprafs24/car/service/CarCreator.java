package ch.uzh.ifi.hase.soprafs24.car.service;

import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.car.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.car.mapper.CarDTOMapper;
import ch.uzh.ifi.hase.soprafs24.car.model.Car;
import ch.uzh.ifi.hase.soprafs24.car.repository.CarRepository;

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