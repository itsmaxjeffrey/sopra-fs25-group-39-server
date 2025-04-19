package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;

class AuthenticatedDriverDTOTest {

    @Test
    void testAuthenticatedDriverDTOFields() {
        AuthenticatedDriverDTO dto = new AuthenticatedDriverDTO();
        
        // Test setting and getting all fields
        dto.setToken("test-token");
        dto.setUserId(1L);
        dto.setUserAccountType(UserAccountType.DRIVER);
        dto.setUsername("driveruser");
        dto.setEmail("driver@example.com");
        dto.setFirstName("Driver");
        dto.setLastName("Test");
        dto.setPhoneNumber("+41791234567");
        dto.setDriverLicensePath("/path/to/license.jpg");
        dto.setDriverInsurancePath("/path/to/insurance.jpg");
        dto.setPreferredRange(50.0f);

        // Create and set location DTO
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLatitude(47.3769);
        locationDTO.setLongitude(8.5417);
        dto.setLocation(locationDTO);

        // Create and set car DTO
        CarDTO carDTO = new CarDTO();
        carDTO.setCarModel("Tesla Model 3");
        carDTO.setLicensePlate("ZH123456");
        dto.setCarDTO(carDTO);

        // Test inherited fields
        assertEquals("test-token", dto.getToken());
        assertEquals(1L, dto.getUserId());
        assertEquals(UserAccountType.DRIVER, dto.getUserAccountType());
        assertEquals("driveruser", dto.getUsername());
        assertEquals("driver@example.com", dto.getEmail());
        assertEquals("Driver", dto.getFirstName());
        assertEquals("Test", dto.getLastName());
        assertEquals("+41791234567", dto.getPhoneNumber());

        // Test driver-specific fields
        assertEquals("/path/to/license.jpg", dto.getDriverLicensePath());
        assertEquals("/path/to/insurance.jpg", dto.getDriverInsurancePath());
        assertEquals(50.0f, dto.getPreferredRange());
        assertEquals(locationDTO, dto.getLocation());
        assertEquals(carDTO, dto.getCarDTO());
    }
} 