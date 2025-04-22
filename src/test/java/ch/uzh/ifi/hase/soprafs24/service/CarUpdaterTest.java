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

class CarUpdaterTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarValidator carValidator;

    @InjectMocks
    private CarUpdater carUpdater;

    private Car existingCar;
    private CarDTO carUpdates;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Create existing car
        existingCar = new Car();
        existingCar.setCarId(1L);
        existingCar.setCarModel("Toyota Camry");
        existingCar.setVolumeCapacity(4.5f);
        existingCar.setWeightCapacity(500.0f);
        existingCar.setElectric(false);
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

        // Mock repository behavior - save should return the car passed to it
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void updateAndSave_success() {
        // when
        Car result = carUpdater.updateAndSave(existingCar, carUpdates);

        // then
        verify(carValidator).validateCar(existingCar);
        verify(carRepository).save(existingCar);
        verify(carRepository).flush();

        assertNotNull(result);
        assertEquals(carUpdates.getCarModel(), result.getCarModel());
        assertEquals(carUpdates.getVolumeCapacity(), result.getVolumeCapacity());
        assertEquals(carUpdates.getWeightCapacity(), result.getWeightCapacity());
        assertEquals(carUpdates.isElectric(), result.isElectric());
        assertEquals(carUpdates.getLicensePlate(), result.getLicensePlate());
        assertEquals(carUpdates.getCarPicturePath(), result.getCarPicturePath());
    }

    @Test
    void updateAndSave_validationFailure_throwsException() {
        // given
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid car data"))
            .when(carValidator).validateCar(existingCar);

        // when/then
        assertThrows(ResponseStatusException.class, () -> carUpdater.updateAndSave(existingCar, carUpdates));
        verify(carValidator).validateCar(existingCar);
        verify(carRepository, never()).save(any(Car.class));
        verify(carRepository, never()).flush();
    }

    @Test
    void updateAndSave_nullUpdates_throwsException() {
        // when/then
        assertThrows(IllegalArgumentException.class, () -> carUpdater.updateAndSave(existingCar, null));
        verify(carValidator, never()).validateCar(any(Car.class));
        verify(carRepository, never()).save(any(Car.class));
        verify(carRepository, never()).flush();
    }
}