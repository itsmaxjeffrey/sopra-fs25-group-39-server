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

public class CarValidatorTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarValidator carValidator;

    private Car testCar;
    private Car existingCar;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test car
        testCar = new Car();
        testCar.setCarId(1L);
        testCar.setCarModel("Tesla Model 3");
        testCar.setVolumeCapacity(100.0f);
        testCar.setWeightCapacity(500.0f);
        testCar.setElectric(true);
        testCar.setLicensePlate("ZH123456");
        testCar.setCarPicturePath("/path/to/car/picture.jpg");

        // Create existing car with same license plate
        existingCar = new Car();
        existingCar.setCarId(2L);
        existingCar.setLicensePlate("ZH123456");
    }

    @Test
    public void validateLicensePlate_uniquePlate_success() {
        // given
        when(carRepository.findByLicensePlate(any())).thenReturn(null);

        // when/then
        assertDoesNotThrow(() -> carValidator.validateLicensePlate(testCar));
        verify(carRepository).findByLicensePlate(testCar.getLicensePlate());
    }

    @Test
    public void validateLicensePlate_duplicatePlate_throwsException() {
        // given
        when(carRepository.findByLicensePlate(any())).thenReturn(existingCar);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> carValidator.validateLicensePlate(testCar));
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("A car with this license plate already exists", exception.getReason());
        verify(carRepository).findByLicensePlate(testCar.getLicensePlate());
    }

    @Test
    public void validateLicensePlate_sameCarId_success() {
        // given
        testCar.setCarId(2L); // Same ID as existingCar
        when(carRepository.findByLicensePlate(any())).thenReturn(existingCar);

        // when/then
        assertDoesNotThrow(() -> carValidator.validateLicensePlate(testCar));
        verify(carRepository).findByLicensePlate(testCar.getLicensePlate());
    }

    @Test
    public void validateLicensePlate_nullLicensePlate_success() {
        // given
        testCar.setLicensePlate(null);

        // when/then
        assertDoesNotThrow(() -> carValidator.validateLicensePlate(testCar));
        verify(carRepository, never()).findByLicensePlate(any());
    }

    @Test
    public void validateLicensePlate_blankLicensePlate_success() {
        // given
        testCar.setLicensePlate("   ");

        // when/then
        assertDoesNotThrow(() -> carValidator.validateLicensePlate(testCar));
        verify(carRepository, never()).findByLicensePlate(any());
    }

    @Test
    public void validateCar_success() {
        // given
        when(carRepository.findByLicensePlate(any())).thenReturn(null);

        // when/then
        assertDoesNotThrow(() -> carValidator.validateCar(testCar));
        verify(carRepository).findByLicensePlate(testCar.getLicensePlate());
    }

    @Test
    public void validateCar_duplicateLicensePlate_throwsException() {
        // given
        when(carRepository.findByLicensePlate(any())).thenReturn(existingCar);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> carValidator.validateCar(testCar));
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("A car with this license plate already exists", exception.getReason());
        verify(carRepository).findByLicensePlate(testCar.getLicensePlate());
    }
} 