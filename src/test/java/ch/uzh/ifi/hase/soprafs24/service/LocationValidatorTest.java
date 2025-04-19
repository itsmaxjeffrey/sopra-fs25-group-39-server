package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class LocationValidatorTest {

    private LocationValidator locationValidator;
    private Location location;

    @BeforeEach
    void setup() {
        locationValidator = new LocationValidator();
        location = new Location();
    }

    @Test
    void validateLocation_validInput_success() {
        // given
        location.setLatitude(47.3769);
        location.setLongitude(8.5417);
        location.setFormattedAddress("Bahnhofstrasse 1, 8001 Zürich");

        // when/then
        assertDoesNotThrow(() -> locationValidator.validateLocation(location));
    }

    @Test
    void validateCoordinates_nullLatitude_throwsException() {
        // given
        location.setLongitude(8.5417);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> locationValidator.validateCoordinates(location));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Both latitude and longitude must be provided", exception.getReason());
    }

    @Test
    void validateCoordinates_nullLongitude_throwsException() {
        // given
        location.setLatitude(47.3769);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> locationValidator.validateCoordinates(location));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Both latitude and longitude must be provided", exception.getReason());
    }

    @Test
    void validateCoordinates_invalidLatitude_throwsException() {
        // given
        location.setLatitude(91.0);
        location.setLongitude(8.5417);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> locationValidator.validateCoordinates(location));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Latitude must be between -90 and 90 degrees", exception.getReason());
    }

    @Test
    void validateCoordinates_invalidLongitude_throwsException() {
        // given
        location.setLatitude(47.3769);
        location.setLongitude(181.0);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> locationValidator.validateCoordinates(location));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Longitude must be between -180 and 180 degrees", exception.getReason());
    }

    @Test
    void validateCoordinates_validBoundaryValues_success() {
        // given
        location.setLatitude(90.0);
        location.setLongitude(180.0);

        // when/then
        assertDoesNotThrow(() -> locationValidator.validateCoordinates(location));
    }

    @Test
    void validateAddress_nullAddress_success() {
        // given
        location.setLatitude(47.3769);
        location.setLongitude(8.5417);
        location.setFormattedAddress(null);

        // when/then
        assertDoesNotThrow(() -> locationValidator.validateAddress(location));
    }

    @Test
    void validateAddress_emptyAddress_throwsException() {
        // given
        location.setLatitude(47.3769);
        location.setLongitude(8.5417);
        location.setFormattedAddress("   ");

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> locationValidator.validateAddress(location));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Formatted address cannot be empty if provided", exception.getReason());
    }

    @Test
    void validateAddress_validAddress_success() {
        // given
        location.setLatitude(47.3769);
        location.setLongitude(8.5417);
        location.setFormattedAddress("Bahnhofstrasse 1, 8001 Zürich");

        // when/then
        assertDoesNotThrow(() -> locationValidator.validateAddress(location));
    }
} 