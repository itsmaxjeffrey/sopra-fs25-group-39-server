package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DriverTest {

    private Driver driver;
    private Car car;
    private Location location;

    @BeforeEach
    void setup() {
        driver = new Driver();
        car = new Car();
        location = new Location();
    }

    @Test
    void testDriverLicensePath() {
        assertNull(driver.getDriverLicensePath());
        
        String licensePath = "/images/license.jpg";
        driver.setDriverLicensePath(licensePath);
        assertEquals(licensePath, driver.getDriverLicensePath());
    }

    @Test
    void testDriverInsurancePath() {
        assertNull(driver.getDriverInsurancePath());
        
        String insurancePath = "/images/insurance.pdf";
        driver.setDriverInsurancePath(insurancePath);
        assertEquals(insurancePath, driver.getDriverInsurancePath());
    }

    @Test
    void testCar() {
        assertNull(driver.getCar());
        
        driver.setCar(car);
        assertEquals(car, driver.getCar());
    }

    @Test
    void testLocation() {
        assertNull(driver.getLocation());
        
        driver.setLocation(location);
        assertEquals(location, driver.getLocation());
    }

    @Test
    void testPreferredRange() {
        assertEquals(0.0f, driver.getPreferredRange());
        
        float range = 50.0f;
        driver.setPreferredRange(range);
        assertEquals(range, driver.getPreferredRange());
    }

    @Test
    void testInheritedFields() {
        // Test inherited fields from User
        Long userId = 1L;
        driver.setUserId(userId);
        assertEquals(userId, driver.getUserId());

        String username = "testdriver";
        driver.setUsername(username);
        assertEquals(username, driver.getUsername());

        String email = "driver@example.com";
        driver.setEmail(email);
        assertEquals(email, driver.getEmail());

        String firstName = "John";
        driver.setFirstName(firstName);
        assertEquals(firstName, driver.getFirstName());

        String lastName = "Doe";
        driver.setLastName(lastName);
        assertEquals(lastName, driver.getLastName());
    }

    @Test
    void testEqualsAndHashCode() {
        Driver driver1 = new Driver();
        Driver driver2 = new Driver();
        
        // Test equals with null
        assertNotEquals(driver1, null);
        
        // Test equals with same object
        assertEquals(driver1, driver1);
        
        // Test equals with different objects but same ID
        driver1.setUserId(1L);
        driver2.setUserId(1L);
        assertEquals(driver1, driver2);
        
        // Test equals with different IDs
        driver2.setUserId(2L);
        assertNotEquals(driver1, driver2);
    }

    @Test
    void testToString() {
        driver.setUserId(1L);
        driver.setUsername("testdriver");
        driver.setEmail("driver@example.com");
        driver.setFirstName("John");
        driver.setLastName("Doe");
        driver.setDriverLicensePath("/images/license.jpg");
        driver.setPreferredRange(50.0f);
        
        String toString = driver.toString();
        assertTrue(toString.contains("userId=1"));
        assertTrue(toString.contains("username=testdriver"));
        assertTrue(toString.contains("email=driver@example.com"));
        assertTrue(toString.contains("firstName=John"));
        assertTrue(toString.contains("lastName=Doe"));
        assertTrue(toString.contains("driverLicensePath=/images/license.jpg"));
        assertTrue(toString.contains("preferredRange=50.0"));
    }
} 