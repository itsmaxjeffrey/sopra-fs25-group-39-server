package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.repository.LocationRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.LocationDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LocationCreatorTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationDTOMapper locationDTOMapper;

    @Mock
    private LocationValidator locationValidator;

    @InjectMocks
    private LocationCreator locationCreator;

    private Location location;
    private LocationDTO locationDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Setup location entity
        location = new Location();
        location.setLatitude(47.3769);
        location.setLongitude(8.5417);
        location.setFormattedAddress("Bahnhofstrasse 1, 8001 Zürich");

        // Setup DTO
        locationDTO = new LocationDTO();
        locationDTO.setLatitude(47.3769);
        locationDTO.setLongitude(8.5417);
        locationDTO.setFormattedAddress("Bahnhofstrasse 1, 8001 Zürich");
    }

    @Test
    void createLocation_validInput_success() {
        // given
        Location savedLocation = new Location();
        savedLocation.setId(1L);
        savedLocation.setLatitude(location.getLatitude());
        savedLocation.setLongitude(location.getLongitude());
        savedLocation.setFormattedAddress(location.getFormattedAddress());
        
        when(locationRepository.save(any())).thenReturn(savedLocation);

        // when
        Location createdLocation = locationCreator.createLocation(location);

        // then
        assertNotNull(createdLocation);
        assertEquals(1L, createdLocation.getId());
        assertEquals(47.3769, createdLocation.getLatitude());
        assertEquals(8.5417, createdLocation.getLongitude());
        assertEquals("Bahnhofstrasse 1, 8001 Zürich", createdLocation.getFormattedAddress());
        
        verify(locationValidator).validateLocation(location);
        verify(locationRepository).save(location);
        verify(locationRepository).flush();
    }

    @Test
    void createLocation_validationFails_throwsException() {
        // given
        doThrow(new RuntimeException("Validation failed"))
            .when(locationValidator)
            .validateLocation(any());

        // when/then
        assertThrows(RuntimeException.class, () -> {
            locationCreator.createLocation(location);
        });

        verify(locationValidator).validateLocation(location);
        verify(locationRepository, never()).save(any());
        verify(locationRepository, never()).flush();
    }

    @Test
    void createLocation_saveFails_throwsException() {
        // given
        when(locationRepository.save(any()))
            .thenThrow(new RuntimeException("Save failed"));

        // when/then
        assertThrows(RuntimeException.class, () -> {
            locationCreator.createLocation(location);
        });

        verify(locationValidator).validateLocation(location);
        verify(locationRepository).save(location);
        verify(locationRepository, never()).flush();
    }

    @Test
    void createLocationFromDTO_validInput_success() {
        // given
        Location mappedLocation = new Location();
        mappedLocation.setLatitude(locationDTO.getLatitude());
        mappedLocation.setLongitude(locationDTO.getLongitude());
        mappedLocation.setFormattedAddress(locationDTO.getFormattedAddress());

        Location savedLocation = new Location();
        savedLocation.setId(1L);
        savedLocation.setLatitude(mappedLocation.getLatitude());
        savedLocation.setLongitude(mappedLocation.getLongitude());
        savedLocation.setFormattedAddress(mappedLocation.getFormattedAddress());

        when(locationDTOMapper.convertLocationDTOToEntity(any())).thenReturn(mappedLocation);
        when(locationRepository.save(any())).thenReturn(savedLocation);

        // when
        Location createdLocation = locationCreator.createLocationFromDTO(locationDTO);

        // then
        assertNotNull(createdLocation);
        assertEquals(1L, createdLocation.getId());
        assertEquals(47.3769, createdLocation.getLatitude());
        assertEquals(8.5417, createdLocation.getLongitude());
        assertEquals("Bahnhofstrasse 1, 8001 Zürich", createdLocation.getFormattedAddress());
        
        verify(locationDTOMapper).convertLocationDTOToEntity(locationDTO);
        verify(locationValidator).validateLocation(mappedLocation);
        verify(locationRepository).save(mappedLocation);
        verify(locationRepository).flush();
    }

    @Test
    void createLocationFromDTO_mappingFails_throwsException() {
        // given
        when(locationDTOMapper.convertLocationDTOToEntity(any()))
            .thenThrow(new RuntimeException("Mapping failed"));

        // when/then
        assertThrows(RuntimeException.class, () -> {
            locationCreator.createLocationFromDTO(locationDTO);
        });

        verify(locationDTOMapper).convertLocationDTOToEntity(locationDTO);
        verify(locationValidator, never()).validateLocation(any());
        verify(locationRepository, never()).save(any());
        verify(locationRepository, never()).flush();
    }

    @Test
    void createLocationFromDTO_validationFails_throwsException() {
        // given
        Location mappedLocation = new Location();
        mappedLocation.setLatitude(locationDTO.getLatitude());
        mappedLocation.setLongitude(locationDTO.getLongitude());
        mappedLocation.setFormattedAddress(locationDTO.getFormattedAddress());

        when(locationDTOMapper.convertLocationDTOToEntity(any())).thenReturn(mappedLocation);
        doThrow(new RuntimeException("Validation failed"))
            .when(locationValidator)
            .validateLocation(any());

        // when/then
        assertThrows(RuntimeException.class, () -> {
            locationCreator.createLocationFromDTO(locationDTO);
        });

        verify(locationDTOMapper).convertLocationDTOToEntity(locationDTO);
        verify(locationValidator).validateLocation(mappedLocation);
        verify(locationRepository, never()).save(any());
        verify(locationRepository, never()).flush();
    }

    @Test
    void createLocationFromDTO_saveFails_throwsException() {
        // given
        Location mappedLocation = new Location();
        mappedLocation.setLatitude(locationDTO.getLatitude());
        mappedLocation.setLongitude(locationDTO.getLongitude());
        mappedLocation.setFormattedAddress(locationDTO.getFormattedAddress());

        when(locationDTOMapper.convertLocationDTOToEntity(any())).thenReturn(mappedLocation);
        when(locationRepository.save(any()))
            .thenThrow(new RuntimeException("Save failed"));

        // when/then
        assertThrows(RuntimeException.class, () -> {
            locationCreator.createLocationFromDTO(locationDTO);
        });

        verify(locationDTOMapper).convertLocationDTOToEntity(locationDTO);
        verify(locationValidator).validateLocation(mappedLocation);
        verify(locationRepository).save(mappedLocation);
        verify(locationRepository, never()).flush();
    }
} 