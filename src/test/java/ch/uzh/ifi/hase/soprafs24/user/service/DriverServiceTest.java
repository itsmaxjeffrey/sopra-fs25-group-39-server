package ch.uzh.ifi.hase.soprafs24.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.service.CarService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.DriverUpdateDTO;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private CarService carService;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private DriverService driverService;

    private Driver driver;
    private DriverUpdateDTO updates;
    private Car initialCar;
    private Location initialLocation;

    @BeforeEach
    void setup() {
        driver = new Driver();
        driver.setUserId(1L);
        driver.setUsername("testdriver");
        driver.setEmail("test@example.com");
        driver.setPassword("password");
        driver.setToken("token");
        driver.setDriverLicensePath("old_license_path");
        driver.setDriverInsurancePath("old_insurance_path");
        driver.setPreferredRange(10.0f);

        initialCar = new Car();
        initialCar.setCarId(99L);
        initialLocation = new Location();
        initialLocation.setId(98L);
        driver.setCar(initialCar);
        driver.setLocation(initialLocation);

        updates = new DriverUpdateDTO();
    }

    @Test
    void testUpdateDriverDetails_AllFields() {
        updates.setDriverLicensePath("new_license_path");
        updates.setDriverInsurancePath("new_insurance_path");
        updates.setPreferredRange(20.0f);

        CarDTO carDTO = new CarDTO();
        LocationDTO locationDTO = new LocationDTO();
        updates.setCar(carDTO);
        updates.setLocation(locationDTO);

        Car updatedCar = new Car();
        Location updatedLocation = new Location();
        when(carService.updateCarFromDTO(eq(initialCar), any(CarDTO.class))).thenReturn(updatedCar);
        when(locationService.updateLocationFromDTO(eq(initialLocation), any(LocationDTO.class))).thenReturn(updatedLocation);

        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        assertEquals("new_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("new_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(20.0f, updatedDriver.getPreferredRange());
        assertNotNull(updatedDriver.getCar());
        assertSame(updatedCar, updatedDriver.getCar());
        assertNotNull(updatedDriver.getLocation());
        assertSame(updatedLocation, updatedDriver.getLocation());

        verify(carService).updateCarFromDTO(eq(initialCar), any(CarDTO.class));
        verify(locationService).updateLocationFromDTO(eq(initialLocation), any(LocationDTO.class));
    }

    @Test
    void testUpdateDriverDetails_NoUpdates() {
        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertNotNull(updatedDriver.getCar());
        assertSame(initialCar, updatedDriver.getCar());
        assertNotNull(updatedDriver.getLocation());
        assertSame(initialLocation, updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyLicense() {
        updates.setDriverLicensePath("new_license_path");

        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        assertEquals("new_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertNotNull(updatedDriver.getCar());
        assertSame(initialCar, updatedDriver.getCar());
        assertNotNull(updatedDriver.getLocation());
        assertSame(initialLocation, updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyInsurance() {
        updates.setDriverInsurancePath("new_insurance_path");

        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("new_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertNotNull(updatedDriver.getCar());
        assertSame(initialCar, updatedDriver.getCar());
        assertNotNull(updatedDriver.getLocation());
        assertSame(initialLocation, updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyPreferredRange() {
        updates.setPreferredRange(30.0f);

        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(30.0f, updatedDriver.getPreferredRange());
        assertNotNull(updatedDriver.getCar());
        assertSame(initialCar, updatedDriver.getCar());
        assertNotNull(updatedDriver.getLocation());
        assertSame(initialLocation, updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyCar() {
        CarDTO carDTO = new CarDTO();
        updates.setCar(carDTO);
        Car updatedCar = new Car();
        when(carService.updateCarFromDTO(eq(initialCar), any(CarDTO.class))).thenReturn(updatedCar);

        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertNotNull(updatedDriver.getCar());
        assertSame(updatedCar, updatedDriver.getCar());
        assertNotNull(updatedDriver.getLocation());
        assertSame(initialLocation, updatedDriver.getLocation());

        verify(carService).updateCarFromDTO(eq(initialCar), any(CarDTO.class));
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyLocation() {
        LocationDTO locationDTO = new LocationDTO();
        updates.setLocation(locationDTO);
        Location updatedLocation = new Location();
        when(locationService.updateLocationFromDTO(eq(initialLocation), any(LocationDTO.class))).thenReturn(updatedLocation);

        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertNotNull(updatedDriver.getCar());
        assertSame(initialCar, updatedDriver.getCar());
        assertNotNull(updatedDriver.getLocation());
        assertSame(updatedLocation, updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService).updateLocationFromDTO(eq(initialLocation), any(LocationDTO.class));
    }
}