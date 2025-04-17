package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.CarDTOMapper;

public class CarCreatorTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarDTOMapper carDTOMapper;

    @Mock
    private CarValidator carValidator;

    @InjectMocks
    private CarCreator carCreator;

    private Car testCar;
    private CarDTO testCarDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test car
        testCar = new Car();
        testCar.setCarModel("Tesla Model 3");
        testCar.setVolumeCapacity(100.0f);
        testCar.setWeightCapacity(500.0f);
        testCar.setElectric(true);
        testCar.setLicensePlate("ZH123456");
        testCar.setCarPicturePath("/path/to/car/picture.jpg");

        // Create test DTO
        testCarDTO = new CarDTO();
        testCarDTO.setCarModel("Tesla Model 3");
        testCarDTO.setVolumeCapacity(100.0f);
        testCarDTO.setWeightCapacity(500.0f);
        testCarDTO.setElectric(true);
        testCarDTO.setLicensePlate("ZH123456");
        testCarDTO.setCarPicturePath("/path/to/car/picture.jpg");

        // Mock mapper behavior
        when(carDTOMapper.convertCarDTOToEntity(any(CarDTO.class))).thenReturn(testCar);
        
        // Mock repository behavior
        when(carRepository.save(any(Car.class))).thenReturn(testCar);
    }

    @Test
    public void createCarFromDTO_success() {
        // when
        Car createdCar = carCreator.createCarFromDTO(testCarDTO);

        // then
        verify(carDTOMapper).convertCarDTOToEntity(testCarDTO);
        verify(carValidator).validateCar(testCar);
        verify(carRepository).save(testCar);
        verify(carRepository).flush();

        assertNotNull(createdCar);
        assertEquals(testCar.getCarModel(), createdCar.getCarModel());
        assertEquals(testCar.getVolumeCapacity(), createdCar.getVolumeCapacity());
        assertEquals(testCar.getWeightCapacity(), createdCar.getWeightCapacity());
        assertEquals(testCar.isElectric(), createdCar.isElectric());
        assertEquals(testCar.getLicensePlate(), createdCar.getLicensePlate());
        assertEquals(testCar.getCarPicturePath(), createdCar.getCarPicturePath());
    }

    @Test
    public void createCar_success() {
        // when
        Car createdCar = carCreator.createCar(testCar);

        // then
        verify(carValidator).validateCar(testCar);
        verify(carRepository).save(testCar);
        verify(carRepository).flush();

        assertNotNull(createdCar);
        assertEquals(testCar.getCarModel(), createdCar.getCarModel());
        assertEquals(testCar.getVolumeCapacity(), createdCar.getVolumeCapacity());
        assertEquals(testCar.getWeightCapacity(), createdCar.getWeightCapacity());
        assertEquals(testCar.isElectric(), createdCar.isElectric());
        assertEquals(testCar.getLicensePlate(), createdCar.getLicensePlate());
        assertEquals(testCar.getCarPicturePath(), createdCar.getCarPicturePath());
    }

    @Test
    public void createCar_validationFailure_throwsException() {
        // given
        doThrow(new RuntimeException("Validation failed")).when(carValidator).validateCar(any(Car.class));

        // when/then
        assertThrows(RuntimeException.class, () -> carCreator.createCar(testCar));
        verify(carValidator).validateCar(testCar);
        verify(carRepository, never()).save(any(Car.class));
        verify(carRepository, never()).flush();
    }
} 