package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.LocationRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationCreator locationCreator;

    @Mock
    private LocationUpdater locationUpdater;

    @InjectMocks
    private LocationService locationService;

    private Location testLocation;
    private LocationDTO testLocationDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test location
        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setFormattedAddress("Test Address");
        testLocation.setLatitude(47.3769);
        testLocation.setLongitude(8.5417);

        // Create test DTO
        testLocationDTO = new LocationDTO();
        testLocationDTO.setFormattedAddress("Test Address");
        testLocationDTO.setLatitude(47.3769);
        testLocationDTO.setLongitude(8.5417);
    }

    @Test
    public void createLocation_validInput_success() {
        // given
        when(locationCreator.createLocation(any())).thenReturn(testLocation);

        // when
        Location createdLocation = locationService.createLocation(testLocation);

        // then
        verify(locationCreator, times(1)).createLocation(any());
        assertEquals(testLocation.getId(), createdLocation.getId());
        assertEquals(testLocation.getFormattedAddress(), createdLocation.getFormattedAddress());
        assertEquals(testLocation.getLatitude(), createdLocation.getLatitude());
        assertEquals(testLocation.getLongitude(), createdLocation.getLongitude());
    }

    @Test
    public void createLocationFromDTO_validInput_success() {
        // given
        when(locationCreator.createLocationFromDTO(any())).thenReturn(testLocation);

        // when
        Location createdLocation = locationService.createLocationFromDTO(testLocationDTO);

        // then
        verify(locationCreator, times(1)).createLocationFromDTO(any());
        assertEquals(testLocation.getId(), createdLocation.getId());
        assertEquals(testLocation.getFormattedAddress(), createdLocation.getFormattedAddress());
        assertEquals(testLocation.getLatitude(), createdLocation.getLatitude());
        assertEquals(testLocation.getLongitude(), createdLocation.getLongitude());
    }

    @Test
    public void updateLocationFromDTO_existingLocation_success() {
        // given
        when(locationUpdater.updateAndSaveLocation(any(), any())).thenReturn(testLocation);

        // when
        Location updatedLocation = locationService.updateLocationFromDTO(testLocation, testLocationDTO);

        // then
        verify(locationUpdater, times(1)).updateAndSaveLocation(any(), any());
        assertEquals(testLocation.getId(), updatedLocation.getId());
        assertEquals(testLocation.getFormattedAddress(), updatedLocation.getFormattedAddress());
        assertEquals(testLocation.getLatitude(), updatedLocation.getLatitude());
        assertEquals(testLocation.getLongitude(), updatedLocation.getLongitude());
    }

    @Test
    public void updateLocationFromDTO_nullLocation_createsNew() {
        // given
        when(locationCreator.createLocationFromDTO(any())).thenReturn(testLocation);

        // when
        Location createdLocation = locationService.updateLocationFromDTO(null, testLocationDTO);

        // then
        verify(locationCreator, times(1)).createLocationFromDTO(any());
        assertEquals(testLocation.getId(), createdLocation.getId());
        assertEquals(testLocation.getFormattedAddress(), createdLocation.getFormattedAddress());
        assertEquals(testLocation.getLatitude(), createdLocation.getLatitude());
        assertEquals(testLocation.getLongitude(), createdLocation.getLongitude());
    }

    @Test
    public void getLocationById_validId_success() {
        // given
        when(locationRepository.findById(any())).thenReturn(java.util.Optional.of(testLocation));

        // when
        Location foundLocation = locationService.getLocationById(1L);

        // then
        verify(locationRepository, times(1)).findById(any());
        assertEquals(testLocation.getId(), foundLocation.getId());
        assertEquals(testLocation.getFormattedAddress(), foundLocation.getFormattedAddress());
        assertEquals(testLocation.getLatitude(), foundLocation.getLatitude());
        assertEquals(testLocation.getLongitude(), foundLocation.getLongitude());
    }

    @Test
    public void getLocationById_invalidId_throwsException() {
        // given
        when(locationRepository.findById(any())).thenReturn(java.util.Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            locationService.getLocationById(999L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Location with ID 999 not found", exception.getReason());
    }
} 