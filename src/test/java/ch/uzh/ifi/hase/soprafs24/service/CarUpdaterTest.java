package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.CarDTOMapper;

class CarUpdaterTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarDTOMapper carDTOMapper;

    @Mock
    private CarValidator carValidator;

    @InjectMocks
    private CarUpdater carUpdater;

    private Car existingCar;
    private CarDTO carUpdates;
    private Car updatedCar;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Create existing car
        existingCar = new Car();
        existingCar.setCarId(1L);
        existingCar.setCarModel("Toyota Camry");
        existingCar.setVolumeCapacity(4.5f);
        existingCar.setWeightCapacity(500.0f);
        existingCar.setElectric(true);
        existingCar.setLicensePlate("ZH123456");
        existingCar.setCarPicturePath("/path/to/car/picture.jpg");

        // Create car updates
        carUpdates = new CarDTO();
        carUpdates.setCarModel("Honda Civic");
        carUpdates.setVolumeCapacity(4.0f);
        carUpdates.setWeightCapacity(450.0f);
        carUpdates.setElectric(true);
        carUpdates.setLicensePlate("ZH654321");
        carUpdates.setCarPicturePath("/path/to/new/car/picture.jpg");

        // Create updated car
        updatedCar = new Car();
        updatedCar.setCarId(1L);
        updatedCar.setCarModel("Honda Civic");
        updatedCar.setVolumeCapacity(4.0f);
        updatedCar.setWeightCapacity(450.0f);
        updatedCar.setElectric(true);
        updatedCar.setLicensePlate("ZH654321");
        updatedCar.setCarPicturePath("/path/to/new/car/picture.jpg");

        // Mock mapper behavior
        when(carDTOMapper.convertCarDTOToEntity(any(CarDTO.class))).thenReturn(updatedCar);
        
        // Mock repository behavior
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);
    }

    @Test
    void updateAndSave_success() {
        // when
        Car result = carUpdater.updateAndSave(existingCar, carUpdates);

        // then
        verify(carDTOMapper).convertCarDTOToEntity(carUpdates);
        verify(carValidator).validateCar(updatedCar);
        verify(carRepository).save(updatedCar);
        verify(carRepository).flush();

        assertNotNull(result);
        assertEquals(updatedCar.getCarModel(), result.getCarModel());
        assertEquals(updatedCar.getVolumeCapacity(), result.getVolumeCapacity());
        assertEquals(updatedCar.getWeightCapacity(), result.getWeightCapacity());
        assertEquals(updatedCar.isElectric(), result.isElectric());
        assertEquals(updatedCar.getLicensePlate(), result.getLicensePlate());
        assertEquals(updatedCar.getCarPicturePath(), result.getCarPicturePath());
    }

    @Test
    void updateAndSave_validationFailure_throwsException() {
        // given
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid car data"))
            .when(carValidator).validateCar(any(Car.class));

        // when/then
        assertThrows(ResponseStatusException.class, () -> carUpdater.updateAndSave(existingCar, carUpdates));
        verify(carDTOMapper).convertCarDTOToEntity(carUpdates);
        verify(carValidator).validateCar(updatedCar);
        verify(carRepository, never()).save(any(Car.class));
        verify(carRepository, never()).flush();
    }

    @Test
    void updateAndSave_nullUpdates_throwsException() {
        // when/then
        assertThrows(IllegalArgumentException.class, () -> carUpdater.updateAndSave(existingCar, null));
        verify(carDTOMapper, never()).convertCarDTOToEntity(any(CarDTO.class));
        verify(carValidator, never()).validateCar(any(Car.class));
        verify(carRepository, never()).save(any(Car.class));
        verify(carRepository, never()).flush();
    }

    @Test
    void updateAndSave_partialUpdates_success() {
        // Arrange
        CarDTO partialUpdates = new CarDTO();
        partialUpdates.setCarModel("Tesla Model 3");
        // space and supportedWeight remain null

        // Create a new car with the expected values
        Car expectedCar = new Car();
        expectedCar.setCarId(1L);
        expectedCar.setCarModel("Tesla Model 3");
        expectedCar.setVolumeCapacity(4.5f); // Preserved from existing car
        expectedCar.setWeightCapacity(500.0f); // Preserved from existing car
        expectedCar.setElectric(true);
        expectedCar.setLicensePlate("ZH123456");
        expectedCar.setCarPicturePath("/path/to/car/picture.jpg");

        // Mock mapper to return the expected car
        when(carDTOMapper.convertCarDTOToEntity(partialUpdates)).thenReturn(expectedCar);
        when(carRepository.save(expectedCar)).thenReturn(expectedCar);

        // Act
        Car updatedCar = carUpdater.updateAndSave(existingCar, partialUpdates);

        // Assert
        assertNotNull(updatedCar);
        assertEquals("Tesla Model 3", updatedCar.getCarModel());
        assertEquals(4.5f, updatedCar.getVolumeCapacity()); // Should keep original value
        assertEquals(500.0f, updatedCar.getWeightCapacity()); // Should keep original value
        verify(carValidator).validateCar(expectedCar);
        verify(carRepository).save(expectedCar);
    }
} 