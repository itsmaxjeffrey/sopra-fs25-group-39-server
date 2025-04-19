package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarTest {

    private Car car;
    private Driver driver;

    @BeforeEach
    void setup() {
        car = new Car();
        driver = new Driver();
    }

    @Test
    void testCarId() {
        // Test initial state
        assertNull(car.getCarId());

        // Test setting and getting carId
        Long carId = 1L;
        car.setCarId(carId);
        assertEquals(carId, car.getCarId());
    }

    @Test
    void testCarModel() {
        // Test initial state
        assertNull(car.getCarModel());

        // Test setting and getting carModel
        String carModel = "Tesla Model 3";
        car.setCarModel(carModel);
        assertEquals(carModel, car.getCarModel());
    }

    @Test
    void testSpace() {
        // Test initial state
        assertEquals(0.0f, car.getSpace());

        // Test setting and getting space
        float space = 2.5f;
        car.setSpace(space);
        assertEquals(space, car.getSpace());
    }

    @Test
    void testSupportedWeight() {
        // Test initial state
        assertEquals(0.0f, car.getSupportedWeight());

        // Test setting and getting supportedWeight
        float weight = 500.0f;
        car.setSupportedWeight(weight);
        assertEquals(weight, car.getSupportedWeight());
    }

    @Test
    void testElectric() {
        // Test initial state
        assertFalse(car.isElectric());

        // Test setting and getting electric
        car.setElectric(true);
        assertTrue(car.isElectric());
    }

    @Test
    void testLicensePlate() {
        // Test initial state
        assertNull(car.getLicensePlate());

        // Test setting and getting licensePlate
        String licensePlate = "ZH123456";
        car.setLicensePlate(licensePlate);
        assertEquals(licensePlate, car.getLicensePlate());
    }

    @Test
    void testCarPicturePath() {
        // Test initial state
        assertNull(car.getCarPicturePath());

        // Test setting and getting carPicturePath
        String picturePath = "/images/cars/tesla.jpg";
        car.setCarPicturePath(picturePath);
        assertEquals(picturePath, car.getCarPicturePath());
    }

    @Test
    void testDriverRelationship() {
        // Test initial state
        assertNull(car.getDriver());

        // Test setting and getting driver
        car.setDriver(driver);
        assertEquals(driver, car.getDriver());
    }

    @Test
    void testEqualsAndHashCode() {
        Car car1 = new Car();
        Car car2 = new Car();
        
        // Test equals with null
        assertFalse(car1.equals(null));
        
        // Test equals with same object
        assertTrue(car1.equals(car1));
        
        // Test equals with different objects but same ID
        car1.setCarId(1L);
        car2.setCarId(1L);
        assertTrue(car1.equals(car2));
        
        // Test equals with different IDs
        car2.setCarId(2L);
        assertFalse(car1.equals(car2));
    }

    @Test
    void testToString() {
        car.setCarId(1L);
        car.setCarModel("Tesla Model 3");
        car.setLicensePlate("ZH123456");
        
        // Since we're using Lombok's @ToString, we can't predict the exact format
        // Just verify that the string contains the important fields
        String toString = car.toString();
        assertTrue(toString.contains("carId=1"));
        assertTrue(toString.contains("carModel=Tesla Model 3"));
        assertTrue(toString.contains("licensePlate=ZH123456"));
    }
} 