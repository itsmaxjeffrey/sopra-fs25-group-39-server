package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;

class DriverRegisterDTOTest {

    @Test
    void testDriverRegisterDTOFields() {
        DriverRegisterDTO dto = new DriverRegisterDTO();
        
        // Test setting and getting all fields
        dto.setUsername("driveruser");
        dto.setPassword("driverpass");
        dto.setEmail("driver@example.com");
        dto.setFirstName("Driver");
        dto.setLastName("Test");
        dto.setPhoneNumber("+41791234567");
        dto.setUserBio("Driver bio");
        dto.setDriverLicensePath("/path/to/license.jpg");
        dto.setDriverInsurancePath("/path/to/insurance.jpg");
        dto.setCarPicturePath("/path/to/car.jpg");
        dto.setCarModel("Tesla Model 3");
        dto.setSpace(100.0f);
        dto.setSupportedWeight(500.0f);
        dto.setElectric(true);
        dto.setLicensePlate("ZH123456");
        dto.setPreferredRange(50.0f);

        assertEquals("driveruser", dto.getUsername());
        assertEquals("driverpass", dto.getPassword());
        assertEquals("driver@example.com", dto.getEmail());
        assertEquals("Driver", dto.getFirstName());
        assertEquals("Test", dto.getLastName());
        assertEquals("+41791234567", dto.getPhoneNumber());
        assertEquals("Driver bio", dto.getUserBio());
        assertEquals("/path/to/license.jpg", dto.getDriverLicensePath());
        assertEquals("/path/to/insurance.jpg", dto.getDriverInsurancePath());
        assertEquals("/path/to/car.jpg", dto.getCarPicturePath());
        assertEquals("Tesla Model 3", dto.getCarModel());
        assertEquals(100.0f, dto.getSpace());
        assertEquals(500.0f, dto.getSupportedWeight());
        assertTrue(dto.isElectric());
        assertEquals("ZH123456", dto.getLicensePlate());
        assertEquals(50.0f, dto.getPreferredRange());
    }

    @Test
    void testUserAccountType() {
        DriverRegisterDTO dto = new DriverRegisterDTO();
        assertEquals(UserAccountType.DRIVER, dto.getUserAccountType());
    }
} 