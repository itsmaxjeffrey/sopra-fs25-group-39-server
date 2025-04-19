package ch.uzh.ifi.hase.soprafs24.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        updates = new DriverUpdateDTO();
    }

    @Test
    void testUpdateDriverDetails_AllFields() {
        // given
        updates.setDriverLicensePath("new_license_path");
        updates.setDriverInsurancePath("new_insurance_path");
        updates.setPreferredRange(20.0f);

        CarDTO carDTO = new CarDTO();
        LocationDTO locationDTO = new LocationDTO();
        updates.setCar(carDTO);
        updates.setLocation(locationDTO);

        Car car = new Car();
        Location location = new Location();
        when(carService.updateCarFromDTO(any(), any())).thenReturn(car);
        when(locationService.updateLocationFromDTO(any(), any())).thenReturn(location);

        // when
        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        // then
        assertEquals("new_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("new_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(20.0f, updatedDriver.getPreferredRange());
        assertEquals(car, updatedDriver.getCar());
        assertEquals(location, updatedDriver.getLocation());

        verify(carService).updateCarFromDTO(any(), any());
        verify(locationService).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_NoUpdates() {
        // given
        // updates DTO is empty

        // when
        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        // then
        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertNull(updatedDriver.getCar());
        assertNull(updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyLicense() {
        // given
        updates.setDriverLicensePath("new_license_path");

        // when
        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        // then
        assertEquals("new_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertNull(updatedDriver.getCar());
        assertNull(updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyInsurance() {
        // given
        updates.setDriverInsurancePath("new_insurance_path");

        // when
        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        // then
        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("new_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertNull(updatedDriver.getCar());
        assertNull(updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyPreferredRange() {
        // given
        updates.setPreferredRange(30.0f);

        // when
        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        // then
        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(30.0f, updatedDriver.getPreferredRange());
        assertNull(updatedDriver.getCar());
        assertNull(updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyCar() {
        // given
        CarDTO carDTO = new CarDTO();
        updates.setCar(carDTO);
        Car car = new Car();
        when(carService.updateCarFromDTO(any(), any())).thenReturn(car);

        // when
        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        // then
        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertEquals(car, updatedDriver.getCar());
        assertNull(updatedDriver.getLocation());

        verify(carService).updateCarFromDTO(any(), any());
        verify(locationService, never()).updateLocationFromDTO(any(), any());
    }

    @Test
    void testUpdateDriverDetails_OnlyLocation() {
        // given
        LocationDTO locationDTO = new LocationDTO();
        updates.setLocation(locationDTO);
        Location location = new Location();
        when(locationService.updateLocationFromDTO(any(), any())).thenReturn(location);

        // when
        Driver updatedDriver = driverService.updateDriverDetails(driver, updates);

        // then
        assertEquals("old_license_path", updatedDriver.getDriverLicensePath());
        assertEquals("old_insurance_path", updatedDriver.getDriverInsurancePath());
        assertEquals(10.0f, updatedDriver.getPreferredRange());
        assertNull(updatedDriver.getCar());
        assertEquals(location, updatedDriver.getLocation());

        verify(carService, never()).updateCarFromDTO(any(), any());
        verify(locationService).updateLocationFromDTO(any(), any());
    }
} 