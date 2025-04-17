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

public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarCreator carCreator;

    @Mock
    private CarUpdater carUpdater;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private CarDTO testCarDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test car
        testCar = new Car();
        testCar.setCarId(1L);
        testCar.setCarModel("Tesla Model 3");
        testCar.setVolumeCapacity(500.0f);
        testCar.setWeightCapacity(1000.0f);
        testCar.setElectric(true);
        testCar.setLicensePlate("ZH123456");
        testCar.setCarPicturePath("/path/to/car.jpg");

        // Create test car DTO
        testCarDTO = new CarDTO();
        testCarDTO.setCarModel("Tesla Model 3");
        testCarDTO.setVolumeCapacity(500.0f);
        testCarDTO.setWeightCapacity(1000.0f);
        testCarDTO.setElectric(true);
        testCarDTO.setLicensePlate("ZH123456");
        testCarDTO.setCarPicturePath("/path/to/car.jpg");
    }

    @Test
    public void createCar_success() {
        // given
        when(carCreator.createCar(any())).thenReturn(testCar);

        // when
        Car createdCar = carService.createCar(testCar);

        // then
        assertNotNull(createdCar);
        assertEquals(testCar.getCarId(), createdCar.getCarId());
        assertEquals(testCar.getCarModel(), createdCar.getCarModel());
        assertEquals(testCar.getVolumeCapacity(), createdCar.getVolumeCapacity());
        assertEquals(testCar.getWeightCapacity(), createdCar.getWeightCapacity());
        assertEquals(testCar.isElectric(), createdCar.isElectric());
        assertEquals(testCar.getLicensePlate(), createdCar.getLicensePlate());
        assertEquals(testCar.getCarPicturePath(), createdCar.getCarPicturePath());

        verify(carCreator).createCar(testCar);
    }

    @Test
    public void createCarFromDTO_success() {
        // given
        when(carCreator.createCarFromDTO(any())).thenReturn(testCar);

        // when
        Car createdCar = carService.createCarFromDTO(testCarDTO);

        // then
        assertNotNull(createdCar);
        assertEquals(testCar.getCarId(), createdCar.getCarId());
        assertEquals(testCar.getCarModel(), createdCar.getCarModel());
        assertEquals(testCar.getVolumeCapacity(), createdCar.getVolumeCapacity());
        assertEquals(testCar.getWeightCapacity(), createdCar.getWeightCapacity());
        assertEquals(testCar.isElectric(), createdCar.isElectric());
        assertEquals(testCar.getLicensePlate(), createdCar.getLicensePlate());
        assertEquals(testCar.getCarPicturePath(), createdCar.getCarPicturePath());

        verify(carCreator).createCarFromDTO(testCarDTO);
    }

    @Test
    public void updateCarFromDTO_existingCar_success() {
        // given
        when(carUpdater.updateAndSave(any(), any())).thenReturn(testCar);

        // when
        Car updatedCar = carService.updateCarFromDTO(testCar, testCarDTO);

        // then
        assertNotNull(updatedCar);
        assertEquals(testCar.getCarId(), updatedCar.getCarId());
        assertEquals(testCar.getCarModel(), updatedCar.getCarModel());
        assertEquals(testCar.getVolumeCapacity(), updatedCar.getVolumeCapacity());
        assertEquals(testCar.getWeightCapacity(), updatedCar.getWeightCapacity());
        assertEquals(testCar.isElectric(), updatedCar.isElectric());
        assertEquals(testCar.getLicensePlate(), updatedCar.getLicensePlate());
        assertEquals(testCar.getCarPicturePath(), updatedCar.getCarPicturePath());

        verify(carUpdater).updateAndSave(testCar, testCarDTO);
    }

    @Test
    public void updateCarFromDTO_nullCar_createsNew() {
        // given
        when(carCreator.createCarFromDTO(any())).thenReturn(testCar);

        // when
        Car createdCar = carService.updateCarFromDTO(null, testCarDTO);

        // then
        assertNotNull(createdCar);
        assertEquals(testCar.getCarId(), createdCar.getCarId());
        assertEquals(testCar.getCarModel(), createdCar.getCarModel());
        assertEquals(testCar.getVolumeCapacity(), createdCar.getVolumeCapacity());
        assertEquals(testCar.getWeightCapacity(), createdCar.getWeightCapacity());
        assertEquals(testCar.isElectric(), createdCar.isElectric());
        assertEquals(testCar.getLicensePlate(), createdCar.getLicensePlate());
        assertEquals(testCar.getCarPicturePath(), createdCar.getCarPicturePath());

        verify(carCreator).createCarFromDTO(testCarDTO);
        verify(carUpdater, never()).updateAndSave(any(), any());
    }

    @Test
    public void getCarById_success() {
        // given
        when(carRepository.findById(any())).thenReturn(java.util.Optional.of(testCar));

        // when
        Car foundCar = carService.getCarById(1L);

        // then
        assertNotNull(foundCar);
        assertEquals(testCar.getCarId(), foundCar.getCarId());
        assertEquals(testCar.getCarModel(), foundCar.getCarModel());
        assertEquals(testCar.getVolumeCapacity(), foundCar.getVolumeCapacity());
        assertEquals(testCar.getWeightCapacity(), foundCar.getWeightCapacity());
        assertEquals(testCar.isElectric(), foundCar.isElectric());
        assertEquals(testCar.getLicensePlate(), foundCar.getLicensePlate());
        assertEquals(testCar.getCarPicturePath(), foundCar.getCarPicturePath());

        verify(carRepository).findById(1L);
    }

    @Test
    public void getCarById_notFound_throwsException() {
        // given
        when(carRepository.findById(any())).thenReturn(java.util.Optional.empty());

        // when/then -> check that an error is thrown
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            carService.getCarById(1L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Car with ID 1 not found", exception.getReason());

        verify(carRepository).findById(1L);
    }
} 