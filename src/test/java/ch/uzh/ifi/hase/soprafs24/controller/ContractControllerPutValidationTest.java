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
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPutDTO;

class ContractControllerPutValidationTest {

    private ContractController contractController;
    private ContractPutDTO validContractPutDTO;
    private Method validateMethod;

    @BeforeEach
    void setUp() throws Exception {
        contractController = new ContractController( null, null, null, null, null);
        
        // Get the private validateContractPutDTO method using reflection
        validateMethod = ContractController.class.getDeclaredMethod("validateContractPutDTO", ContractPutDTO.class);
        validateMethod.setAccessible(true);
        
        // Create a valid contract DTO
        validContractPutDTO = new ContractPutDTO();
        validContractPutDTO.setTitle("Test Contract");
        validContractPutDTO.setWeight(10.0);
        validContractPutDTO.setHeight(2.0);
        validContractPutDTO.setWidth(1.5);
        validContractPutDTO.setLength(3.0);
        validContractPutDTO.setManPower(2);
        validContractPutDTO.setPrice(100.0);
        // validContractPutDTO.setCollateral(50.0);
        validContractPutDTO.setMoveDateTime(LocalDateTime.now().plusDays(1));
        
        // Set valid locations
        LocationDTO fromLocation = new LocationDTO();
        fromLocation.setLatitude(47.3769);
        fromLocation.setLongitude(8.5417);
        validContractPutDTO.setFromLocation(fromLocation);
        
        LocationDTO toLocation = new LocationDTO();
        toLocation.setLatitude(47.3769);
        toLocation.setLongitude(8.5418);
        validContractPutDTO.setToLocation(toLocation);
    }

    private ResponseStatusException invokeValidateAndUnwrapException(ContractPutDTO dto) {
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
    void validateContractPutDTO_validData_success() {
        // Should not throw any exception
        assertDoesNotThrow(() -> {
            try {
                validateMethod.invoke(contractController, validContractPutDTO);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void validateContractPutDTO_emptyTitle_throwsException() {
        validContractPutDTO.setTitle("");
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Title cannot be empty", exception.getReason());
    }

    @Test
    void validateContractPutDTO_pastMoveDateTime_throwsException() {
        validContractPutDTO.setMoveDateTime(LocalDateTime.now().minusDays(1));
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Move date time must be in the future", exception.getReason());
    }

    @Test
    void validateContractPutDTO_negativeWeight_throwsException() {
        validContractPutDTO.setWeight(-1.0);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Weight must be positive", exception.getReason());
    }

    @Test
    void validateContractPutDTO_negativeHeight_throwsException() {
        validContractPutDTO.setHeight(-1.0);
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Height must be positive", exception.getReason());
    }

    @Test
    void validateContractPutDTO_zeroHeight_throwsException() {
        validContractPutDTO.setHeight(0.0);
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Height must be positive", exception.getReason());
    }

    @Test
    void validateContractPutDTO_negativeWidth_throwsException() {
        validContractPutDTO.setWidth(-1.0);
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Width must be positive", exception.getReason());
    }

    @Test
    void validateContractPutDTO_zeroWidth_throwsException() {
        validContractPutDTO.setWidth(0.0);
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Width must be positive", exception.getReason());
    }

    @Test
    void validateContractPutDTO_negativeLength_throwsException() {
        validContractPutDTO.setLength(-1.0);
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Length must be positive", exception.getReason());
    }

    @Test
    void validateContractPutDTO_zeroLength_throwsException() {
        validContractPutDTO.setLength(0.0);
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Length must be positive", exception.getReason());
    }

    @Test
    void validateContractPutDTO_negativeManPower_throwsException() {
        validContractPutDTO.setManPower(-1);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Man power must be positive", exception.getReason());
    }

    @Test
    void validateContractPutDTO_negativePrice_throwsException() {
        validContractPutDTO.setPrice(-1.0);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("Price must be positive", exception.getReason());
    }

    // @Test
    // void validateContractPutDTO_negativeCollateral_throwsException() {
    //     validContractPutDTO.setCollateral(-1.0);
        
    //     ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
    //     assertEquals("Collateral must be positive", exception.getReason());
    // }

    @Test
    void validateContractPutDTO_invalidFromLocation_throwsException() {
        validContractPutDTO.getFromLocation().setLatitude(null);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("From location must have a valid latitude and longitude", exception.getReason());
    }

    @Test
    void validateContractPutDTO_invalidToLocation_throwsException() {
        validContractPutDTO.getToLocation().setLongitude(null);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("To location must have a valid latitude and longitude", exception.getReason());
    }

    @Test
    void validateContractPutDTO_sameLocations_throwsException() {
        // Set same coordinates for both locations
        validContractPutDTO.getToLocation().setLatitude(47.3769);
        validContractPutDTO.getToLocation().setLongitude(8.5417);
        
        ResponseStatusException exception = invokeValidateAndUnwrapException(validContractPutDTO);
        assertEquals("From and to locations cannot be the same", exception.getReason());
    }

    // @Test
    // void validateContractPutDTO_partialUpdate_success() {
    //     // Create a DTO with only some fields updated
    //     ContractPutDTO partialUpdate = new ContractPutDTO();
    //     partialUpdate.setTitle("Updated Title");
    //     partialUpdate.setPrice(150.0);
        
    //     // Should not throw any exception
    //     assertDoesNotThrow(() -> {
    //         try {
    //             validateMethod.invoke(contractController, partialUpdate);
    //         } catch (InvocationTargetException e) {
    //             throw e.getCause();
    //         }
    //     });
    // }
}