package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedDriverDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedRequesterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedUserDTO;

class UserDTOMapperTest {

    private final UserDTOMapper userDTOMapper = new UserDTOMapper();

    @Test
    void testConvertToBaseUserDTO() {
        // Create a base user
        User user = new User();
        user.setUserId(1L);
        user.setToken("test-token");
        user.setUserAccountType(UserAccountType.DRIVER);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("+41791234567");
        user.setWalletBalance(100.0);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setUserBio("Test bio");
        user.setProfilePicturePath("/path/to/picture.jpg");

        // Convert to DTO
        AuthenticatedUserDTO dto = userDTOMapper.convertToDTO(user);

        // Verify all fields are mapped correctly
        assertEquals(user.getToken(), dto.getToken());
        assertEquals(user.getUserId(), dto.getUserId());
        assertEquals(user.getUserAccountType(), dto.getUserAccountType());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getFirstName(), dto.getFirstName());
        assertEquals(user.getLastName(), dto.getLastName());
        assertEquals(user.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(user.getWalletBalance(), dto.getWalletBalance());
        assertEquals(user.getBirthDate(), dto.getBirthDate());
        assertEquals(user.getUserBio(), dto.getUserBio());
        assertEquals(user.getProfilePicturePath(), dto.getProfilePicturePath());
    }

    @Test
    void testConvertToDriverDTO() {
        // Create a driver with car and location
        Driver driver = new Driver();
        driver.setUserId(1L);
        driver.setToken("test-token");
        driver.setUserAccountType(UserAccountType.DRIVER);
        driver.setUsername("driveruser");
        driver.setEmail("driver@example.com");
        driver.setFirstName("Driver");
        driver.setLastName("Test");
        driver.setPhoneNumber("+41791234567");
        driver.setWalletBalance(100.0);
        driver.setBirthDate(LocalDate.of(1990, 1, 1));
        driver.setUserBio("Driver bio");
        driver.setProfilePicturePath("/path/to/picture.jpg");
        driver.setDriverLicensePath("/path/to/license.jpg");
        driver.setDriverInsurancePath("/path/to/insurance.jpg");
        driver.setPreferredRange(50.0f);

        // Create and set car
        Car car = new Car();
        car.setCarId(1L);
        car.setCarModel("Tesla Model 3");
        car.setVolumeCapacity(100.0f);
        car.setWeightCapacity(500.0f);
        car.setElectric(true);
        car.setLicensePlate("ZH123456");
        car.setCarPicturePath("/path/to/car.jpg");
        driver.setCar(car);

        // Create and set location
        Location location = new Location();
        location.setId(1L);
        location.setLatitude(47.3769);
        location.setLongitude(8.5417);
        location.setFormattedAddress("Zurich, Switzerland");
        driver.setLocation(location);

        // Convert to DTO
        AuthenticatedDriverDTO dto = (AuthenticatedDriverDTO) userDTOMapper.convertToDTO(driver);

        // Verify all fields are mapped correctly
        assertEquals(driver.getToken(), dto.getToken());
        assertEquals(driver.getUserId(), dto.getUserId());
        assertEquals(driver.getUserAccountType(), dto.getUserAccountType());
        assertEquals(driver.getUsername(), dto.getUsername());
        assertEquals(driver.getEmail(), dto.getEmail());
        assertEquals(driver.getFirstName(), dto.getFirstName());
        assertEquals(driver.getLastName(), dto.getLastName());
        assertEquals(driver.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(driver.getWalletBalance(), dto.getWalletBalance());
        assertEquals(driver.getBirthDate(), dto.getBirthDate());
        assertEquals(driver.getUserBio(), dto.getUserBio());
        assertEquals(driver.getProfilePicturePath(), dto.getProfilePicturePath());
        assertEquals(driver.getDriverLicensePath(), dto.getDriverLicensePath());
        assertEquals(driver.getDriverInsurancePath(), dto.getDriverInsurancePath());
        assertEquals(driver.getPreferredRange(), dto.getPreferredRange());

        // Verify car mapping
        assertNotNull(dto.getCarDTO());
        assertEquals(car.getCarId(), dto.getCarDTO().getCarId());
        assertEquals(car.getCarModel(), dto.getCarDTO().getCarModel());
        assertEquals(car.getVolumeCapacity(), dto.getCarDTO().getVolumeCapacity());
        assertEquals(car.getWeightCapacity(), dto.getCarDTO().getWeightCapacity());
        assertEquals(car.isElectric(), dto.getCarDTO().isElectric());
        assertEquals(car.getLicensePlate(), dto.getCarDTO().getLicensePlate());
        assertEquals(car.getCarPicturePath(), dto.getCarDTO().getCarPicturePath());
        assertEquals(driver.getUserId(), dto.getCarDTO().getDriverId());

        // Verify location mapping
        assertNotNull(dto.getLocation());
        assertEquals(location.getId(), dto.getLocation().getId());
        assertEquals(location.getLatitude(), dto.getLocation().getLatitude());
        assertEquals(location.getLongitude(), dto.getLocation().getLongitude());
        assertEquals(location.getFormattedAddress(), dto.getLocation().getFormattedAddress());
    }

    @Test
    void testConvertToRequesterDTO() {
        // Create a requester
        Requester requester = new Requester();
        requester.setUserId(1L);
        requester.setToken("test-token");
        requester.setUserAccountType(UserAccountType.REQUESTER);
        requester.setUsername("requesteruser");
        requester.setEmail("requester@example.com");
        requester.setFirstName("Requester");
        requester.setLastName("Test");
        requester.setPhoneNumber("+41791234567");
        requester.setWalletBalance(100.0);
        requester.setBirthDate(LocalDate.of(1990, 1, 1));
        requester.setUserBio("Requester bio");
        requester.setProfilePicturePath("/path/to/picture.jpg");

        // Convert to DTO
        AuthenticatedRequesterDTO dto = (AuthenticatedRequesterDTO) userDTOMapper.convertToDTO(requester);

        // Verify all fields are mapped correctly
        assertEquals(requester.getToken(), dto.getToken());
        assertEquals(requester.getUserId(), dto.getUserId());
        assertEquals(requester.getUserAccountType(), dto.getUserAccountType());
        assertEquals(requester.getUsername(), dto.getUsername());
        assertEquals(requester.getEmail(), dto.getEmail());
        assertEquals(requester.getFirstName(), dto.getFirstName());
        assertEquals(requester.getLastName(), dto.getLastName());
        assertEquals(requester.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(requester.getWalletBalance(), dto.getWalletBalance());
        assertEquals(requester.getBirthDate(), dto.getBirthDate());
        assertEquals(requester.getUserBio(), dto.getUserBio());
        assertEquals(requester.getProfilePicturePath(), dto.getProfilePicturePath());
    }

    @Test
    void testConvertToDriverDTOWithoutCarAndLocation() {
        // Create a driver without car and location
        Driver driver = new Driver();
        driver.setUserId(1L);
        driver.setToken("test-token");
        driver.setUserAccountType(UserAccountType.DRIVER);
        driver.setUsername("driveruser");
        driver.setEmail("driver@example.com");
        driver.setFirstName("Driver");
        driver.setLastName("Test");
        driver.setPhoneNumber("+41791234567");
        driver.setWalletBalance(100.0);
        driver.setBirthDate(LocalDate.of(1990, 1, 1));
        driver.setUserBio("Driver bio");
        driver.setProfilePicturePath("/path/to/picture.jpg");
        driver.setDriverLicensePath("/path/to/license.jpg");
        driver.setDriverInsurancePath("/path/to/insurance.jpg");
        driver.setPreferredRange(50.0f);

        // Convert to DTO
        AuthenticatedDriverDTO dto = (AuthenticatedDriverDTO) userDTOMapper.convertToDTO(driver);

        // Verify car and location are null
        assertNull(dto.getCarDTO());
        assertNull(dto.getLocation());
    }
} 