package ch.uzh.ifi.hase.soprafs24.rest.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CarDTOTest {

    @Test
    void testCarDTOFields() {
        CarDTO dto = new CarDTO();
        
        // Test setting and getting all fields
        dto.setCarId(1L);
        dto.setCarModel("Tesla Model 3");
        dto.setVolumeCapacity(100.0f);
        dto.setWeightCapacity(500.0f);
        dto.setElectric(true);
        dto.setLicensePlate("ZH123456");
        dto.setCarPicturePath("/path/to/car.jpg");
        dto.setDriverId(2L);

        assertEquals(1L, dto.getCarId());
        assertEquals("Tesla Model 3", dto.getCarModel());
        assertEquals(100.0f, dto.getVolumeCapacity());
        assertEquals(500.0f, dto.getWeightCapacity());
        assertTrue(dto.isElectric());
        assertEquals("ZH123456", dto.getLicensePlate());
        assertEquals("/path/to/car.jpg", dto.getCarPicturePath());
        assertEquals(2L, dto.getDriverId());
    }

    @Test
    void testElectricFalse() {
        CarDTO dto = new CarDTO();
        dto.setElectric(false);
        assertFalse(dto.isElectric());
    }

    @Test
    void testNullFields() {
        CarDTO dto = new CarDTO();
        dto.setCarModel(null);
        dto.setLicensePlate(null);
        dto.setCarPicturePath(null);

        assertEquals(null, dto.getCarModel());
        assertEquals(null, dto.getLicensePlate());
        assertEquals(null, dto.getCarPicturePath());
    }

    @Test
    void testZeroValues() {
        CarDTO dto = new CarDTO();
        dto.setVolumeCapacity(0.0f);
        dto.setWeightCapacity(0.0f);

        assertEquals(0.0f, dto.getVolumeCapacity());
        assertEquals(0.0f, dto.getWeightCapacity());
    }
} 