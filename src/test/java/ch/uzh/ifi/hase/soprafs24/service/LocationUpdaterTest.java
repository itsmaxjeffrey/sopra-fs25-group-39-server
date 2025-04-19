package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.LocationRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LocationUpdaterTest {

    @Mock
    private LocationValidator locationValidator;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationUpdater locationUpdater;

    private Location existingLocation;
    private LocationDTO locationDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Setup existing location
        existingLocation = new Location();
        existingLocation.setId(1L);
        existingLocation.setLatitude(47.3769);
        existingLocation.setLongitude(8.5417);
        existingLocation.setFormattedAddress("Old Address");

        // Setup DTO with new values
        locationDTO = new LocationDTO();
        locationDTO.setLatitude(47.3770);
        locationDTO.setLongitude(8.5418);
        locationDTO.setFormattedAddress("New Address");
    }

    @Test
    void updateAndSaveLocation_allFieldsUpdated_success() {
        // given
        when(locationRepository.save(any())).thenReturn(existingLocation);

        // when
        Location updatedLocation = locationUpdater.updateAndSaveLocation(existingLocation, locationDTO);

        // then
        assertNotNull(updatedLocation);
        assertEquals(47.3770, updatedLocation.getLatitude());
        assertEquals(8.5418, updatedLocation.getLongitude());
        assertEquals("New Address", updatedLocation.getFormattedAddress());
        
        verify(locationValidator).validateLocation(existingLocation);
        verify(locationRepository).save(existingLocation);
        verify(locationRepository).flush();
    }

    @Test
    void updateAndSaveLocation_partialUpdate_success() {
        // given
        locationDTO.setLatitude(null); // Don't update latitude
        locationDTO.setFormattedAddress(null); // Don't update address
        when(locationRepository.save(any())).thenReturn(existingLocation);

        // when
        Location updatedLocation = locationUpdater.updateAndSaveLocation(existingLocation, locationDTO);

        // then
        assertNotNull(updatedLocation);
        assertEquals(47.3769, updatedLocation.getLatitude()); // Original value
        assertEquals(8.5418, updatedLocation.getLongitude()); // Updated value
        assertEquals("Old Address", updatedLocation.getFormattedAddress()); // Original value
        
        verify(locationValidator).validateLocation(existingLocation);
        verify(locationRepository).save(existingLocation);
        verify(locationRepository).flush();
    }

    @Test
    void updateAndSaveLocation_noUpdates_success() {
        // given
        LocationDTO emptyDTO = new LocationDTO();
        when(locationRepository.save(any())).thenReturn(existingLocation);

        // when
        Location updatedLocation = locationUpdater.updateAndSaveLocation(existingLocation, emptyDTO);

        // then
        assertNotNull(updatedLocation);
        assertEquals(47.3769, updatedLocation.getLatitude());
        assertEquals(8.5417, updatedLocation.getLongitude());
        assertEquals("Old Address", updatedLocation.getFormattedAddress());
        
        verify(locationValidator).validateLocation(existingLocation);
        verify(locationRepository).save(existingLocation);
        verify(locationRepository).flush();
    }

    @Test
    void updateAndSaveLocation_validationFails_throwsException() {
        // given
        doThrow(new RuntimeException("Validation failed"))
            .when(locationValidator)
            .validateLocation(any());

        // when/then
        assertThrows(RuntimeException.class, () -> {
            locationUpdater.updateAndSaveLocation(existingLocation, locationDTO);
        });

        verify(locationValidator).validateLocation(existingLocation);
        verify(locationRepository, never()).save(any());
        verify(locationRepository, never()).flush();
    }

    @Test
    void updateAndSaveLocation_saveFails_throwsException() {
        // given
        when(locationRepository.save(any()))
            .thenThrow(new RuntimeException("Save failed"));

        // when/then
        assertThrows(RuntimeException.class, () -> {
            locationUpdater.updateAndSaveLocation(existingLocation, locationDTO);
        });

        verify(locationValidator).validateLocation(existingLocation);
        verify(locationRepository).save(existingLocation);
        verify(locationRepository, never()).flush();
    }
} 