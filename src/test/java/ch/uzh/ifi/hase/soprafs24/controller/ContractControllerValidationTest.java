package ch.uzh.ifi.hase.soprafs24.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPostDTO;

class ContractControllerValidationTest {

    private ContractController contractController;
    private ContractPostDTO validContractPostDTO;
    private Method validateMethod;

    @BeforeEach
    void setUp() throws Exception {
        contractController = new ContractController( null, null, null, null, null);

        // Get the private validateContractPostDTO method using reflection
        validateMethod = ContractController.class.getDeclaredMethod("validateContractPostDTO", ContractPostDTO.class);
        validateMethod.setAccessible(true);

        // Create a valid contract DTO
        validContractPostDTO = new ContractPostDTO();
        validContractPostDTO.setTitle("Test Contract");
        validContractPostDTO.setWeight(10.0);
        validContractPostDTO.setHeight(2.0); // Use height
        validContractPostDTO.setWidth(1.5); // Use width
        validContractPostDTO.setLength(3.0); // Use length
        validContractPostDTO.setManPower(2);
        validContractPostDTO.setPrice(100.0);
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
            // Use instanceof pattern matching
            if (e.getCause() instanceof ResponseStatusException rse) {
                return rse;
            }
            // Re-throw cause if it's a RuntimeException
            if (e.getCause() instanceof RuntimeException runtimeCause) {
                throw runtimeCause;
            }
            // Wrap other causes
            throw new RuntimeException("Unexpected exception type during validation", e.getCause());
        } catch (IllegalAccessException | IllegalArgumentException e) { // Catch specific reflection exceptions
            // These indicate a problem with the test setup (reflection call) itself
            throw new RuntimeException("Error invoking validation method via reflection", e);
        }
    }

    @Test
    void validateContractPostDTO_validData_success() {
        // Should not throw any exception
        assertDoesNotThrow(() -> {
            try {
                validateMethod.invoke(contractController, validContractPostDTO);
            } catch (InvocationTargetException e) {
                // If invoke throws, unwrap the actual cause
                if (e.getCause() != null) {
                    throw e.getCause();
                }
                throw e; // Re-throw original if no cause
            }
        });
    }

    @Test
    void validateContractPostDTO_missingLocations_throwsException() {
        validContractPostDTO.setFromLocation(null);
        validContractPostDTO.setToLocation(null);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("From and to locations are required", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_invalidFromLocation_throwsException() {
        validContractPostDTO.getFromLocation().setLatitude(null);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("From location must have a valid latitude and longitude", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_invalidToLocation_throwsException() {
        validContractPostDTO.getToLocation().setLongitude(null);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("To location must have a valid latitude and longitude", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_sameLocations_throwsException() {
        // Set same coordinates for both locations
        validContractPostDTO.getToLocation().setLatitude(47.3769);
        validContractPostDTO.getToLocation().setLongitude(8.5417);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("From and to locations cannot be the same", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_missingRequesterId_throwsException() {
        validContractPostDTO.setRequesterId(null);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Requester ID is required", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_emptyTitle_throwsException() {
        validContractPostDTO.setTitle("");

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Title is required", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_nullTitle_throwsException() {
        validContractPostDTO.setTitle(null);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Title is required", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_negativeWeight_throwsException() {
        validContractPostDTO.setWeight(-1.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Weight must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_zeroWeight_throwsException() {
        validContractPostDTO.setWeight(0.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Weight must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_negativeHeight_throwsException() {
        validContractPostDTO.setHeight(-1.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Height must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_zeroHeight_throwsException() {
        validContractPostDTO.setHeight(0.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Height must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_negativeWidth_throwsException() {
        validContractPostDTO.setWidth(-1.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Width must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_zeroWidth_throwsException() {
        validContractPostDTO.setWidth(0.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Width must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_negativeLength_throwsException() {
        validContractPostDTO.setLength(-1.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Length must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_zeroLength_throwsException() {
        validContractPostDTO.setLength(0.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Length must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_negativePrice_throwsException() {
        validContractPostDTO.setPrice(-1.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Price must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_zeroPrice_throwsException() {
        validContractPostDTO.setPrice(0.0);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Price must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

    @Test
    void validateContractPostDTO_negativeManPower_throwsException() {
        validContractPostDTO.setManPower(-1);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Man power must be positive", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }


    @Test
    void validateContractPostDTO_missingMoveDateTime_throwsException() {
        validContractPostDTO.setMoveDateTime(null);

        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPostDTO);
        assertEquals("Move date time is required", exception.getReason());
        assertEquals(400, exception.getRawStatusCode()); // Check status code
    }

}