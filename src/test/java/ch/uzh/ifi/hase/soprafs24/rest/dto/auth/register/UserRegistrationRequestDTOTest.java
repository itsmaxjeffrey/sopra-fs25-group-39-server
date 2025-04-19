package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;

class UserRegistrationRequestDTOTest {

    @Test
    void testUserRegistrationRequestDTOFields() {
        UserRegistrationRequestDTO dto = new UserRegistrationRequestDTO();
        
        // Create and set a driver DTO
        DriverRegisterDTO driverDTO = new DriverRegisterDTO();
        driverDTO.setUsername("driveruser");
        driverDTO.setPassword("driverpass");
        
        // Create and set a car DTO
        CarDTO carDTO = new CarDTO();
        carDTO.setCarModel("Tesla Model 3");
        carDTO.setLicensePlate("ZH123456");
        
        // Create and set a location DTO
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLatitude(47.3769);
        locationDTO.setLongitude(8.5417);
        
        // Set all fields
        dto.setUser(driverDTO);
        dto.setCar(carDTO);
        dto.setLocation(locationDTO);

        assertEquals(driverDTO, dto.getUser());
        assertEquals(carDTO, dto.getCar());
        assertEquals(locationDTO, dto.getLocation());
    }

    @Test
    void testOptionalFields() {
        UserRegistrationRequestDTO dto = new UserRegistrationRequestDTO();
        
        // Create and set a requester DTO
        RequesterRegisterDTO requesterDTO = new RequesterRegisterDTO();
        requesterDTO.setUsername("requesteruser");
        requesterDTO.setPassword("requesterpass");
        
        // Set only the user field
        dto.setUser(requesterDTO);

        assertEquals(requesterDTO, dto.getUser());
        assertNull(dto.getCar());
        assertNull(dto.getLocation());
    }
} 