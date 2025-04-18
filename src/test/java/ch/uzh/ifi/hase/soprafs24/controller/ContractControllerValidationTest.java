package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ContractControllerValidationTest {

    private ContractController contractController;
    private ContractPostDTO validContractPostDTO;
    private Method validateMethod;

    @BeforeEach
    void setUp() throws Exception {
        contractController = new ContractController(null, null, null, null, null, null);
        
        // Get the private validateContractPostDTO method using reflection
        validateMethod = ContractController.class.getDeclaredMethod("validateContractPostDTO", ContractPostDTO.class);
        validateMethod.setAccessible(true);
        
        // Create a valid contract DTO
        validContractPostDTO = new ContractPostDTO();
        validContractPostDTO.setTitle("Test Contract");
        validContractPostDTO.setMass(10.0f);
        validContractPostDTO.setVolume(5.0f);
        validContractPostDTO.setManPower(2);
        validContractPostDTO.setPrice(100.0f);
        validContractPostDTO.setCollateral(50.0f);
        validContractPostDTO.setMoveDateTime(LocalDateTime.now().plusDays(1));
        
        // Set valid locations
        LocationDTO fromLocation = new LocationDTO();
        fromLocation.setLatitude(47.3769);
        fromLocation.setLongitude(8.5417);
        validContractPostDTO.setFromLocation(fromLocation);
        
        LocationDTO toLocation = new LocationDTO();
        toLocation.setLatitude(47.3769);
        toLocation.setLongitude(8.5418);
        validContractPostDTO.setToLocation(toLocation);
        
        validContractPostDTO.setRequesterId(1L);
    }

    private ResponseStatusException invokeValidateAndUnwrapException(ContractPostDTO dto) {
        try {
            validateMethod.invoke(contractController, dto);
            fail("Expected ResponseStatusException to be thrown");
            return null;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof ResponseStatusException) {
                return (ResponseStatusException) e.getCause();
            }
            throw new RuntimeException("Unexpected exception type", e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during validation", e);
        }
    }

    @Test
    void validateContractPostDTO_validData_success() {
        // Should not throw any exception
        assertDoesNotThrow(() -> {
            try {
                validateMethod.invoke(contractController, validContractPostDTO);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void validateContractPostDTO_missingLocations_throwsException() {
        validContractPostDTO.setFromLocation(null);
        validContractPostDTO.setToLocation(null);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("From and to locations are required", exception.getReason());
    }

    @Test
    void validateContractPostDTO_invalidFromLocation_throwsException() {
        validContractPostDTO.getFromLocation().setLatitude(null);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("From location must have a valid latitude and longitude", exception.getReason());
    }

    @Test
    void validateContractPostDTO_invalidToLocation_throwsException() {
        validContractPostDTO.getToLocation().setLongitude(null);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("To location must have a valid latitude and longitude", exception.getReason());
    }

    @Test
    void validateContractPostDTO_sameLocations_throwsException() {
        // Set same coordinates for both locations
        validContractPostDTO.getToLocation().setLatitude(47.3769);
        validContractPostDTO.getToLocation().setLongitude(8.5417);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("From and to locations cannot be the same", exception.getReason());
    }

    @Test
    void validateContractPostDTO_missingRequesterId_throwsException() {
        validContractPostDTO.setRequesterId(null);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Requester ID is required", exception.getReason());
    }

    @Test
    void validateContractPostDTO_emptyTitle_throwsException() {
        validContractPostDTO.setTitle("");
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Title is required", exception.getReason());
    }

    @Test
    void validateContractPostDTO_nullTitle_throwsException() {
        validContractPostDTO.setTitle(null);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Title is required", exception.getReason());
    }

    @Test
    void validateContractPostDTO_pastMoveDateTime_throwsException() {
        validContractPostDTO.setMoveDateTime(LocalDateTime.now().minusDays(1));
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Move date time must be in the future", exception.getReason());
    }

    @Test
    void validateContractPostDTO_negativeMass_throwsException() {
        validContractPostDTO.setMass(-1.0f);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Mass must be positive", exception.getReason());
    }

    @Test
    void validateContractPostDTO_negativeVolume_throwsException() {
        validContractPostDTO.setVolume(-1.0f);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Volume must be positive", exception.getReason());
    }

    @Test
    void validateContractPostDTO_negativeManPower_throwsException() {
        validContractPostDTO.setManPower(-1);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Man power must be positive", exception.getReason());
    }

    @Test
    void validateContractPostDTO_negativePrice_throwsException() {
        validContractPostDTO.setPrice(-1.0f);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Price must be positive", exception.getReason());
    }

    @Test
    void validateContractPostDTO_negativeCollateral_throwsException() {
        validContractPostDTO.setCollateral(-1.0f);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Collateral must be positive", exception.getReason());
    }
} 