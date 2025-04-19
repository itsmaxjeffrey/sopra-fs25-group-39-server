package ch.uzh.ifi.hase.soprafs24.security.registration.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.DriverRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.RequesterRegisterDTO;

class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DriverRegistrationService driverRegistrationService;

    @Mock
    private RequesterRegistrationService requesterRegistrationService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    private DriverRegisterDTO driverRegisterDTO;
    private RequesterRegisterDTO requesterRegisterDTO;
    private CarDTO carDTO;
    private LocationDTO locationDTO;
    private Driver testDriver;
    private Requester testRequester;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test driver
        testDriver = new Driver();
        testDriver.setUsername("testuser");
        testDriver.setPassword("password");
        testDriver.setToken("test-token");

        // Create test requester
        testRequester = new Requester();
        testRequester.setUsername("testrequester");
        testRequester.setPassword("password");
        testRequester.setToken("test-token");

        // Create test DTOs
        driverRegisterDTO = new DriverRegisterDTO();
        driverRegisterDTO.setUsername("testuser");
        driverRegisterDTO.setPassword("password");
        driverRegisterDTO.setEmail("test@example.com");
        driverRegisterDTO.setUserAccountType(UserAccountType.DRIVER);

        requesterRegisterDTO = new RequesterRegisterDTO();
        requesterRegisterDTO.setUsername("testrequester");
        requesterRegisterDTO.setPassword("password");
        requesterRegisterDTO.setEmail("requester@example.com");
        requesterRegisterDTO.setUserAccountType(UserAccountType.REQUESTER);

        carDTO = new CarDTO();
        locationDTO = new LocationDTO();
    }

    @Test
    void registerDriver_success() {
        // given
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(driverRegistrationService.registerDriver(any(), any(), any())).thenReturn(testDriver);
        when(tokenService.generateToken()).thenReturn("test-token");
        when(userRepository.save(any())).thenReturn(testDriver);

        // when
        User result = userRegistrationService.registerUser(driverRegisterDTO, carDTO, locationDTO);

        // then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test-token", result.getToken());
        verify(userRepository).save(any());
    }

    @Test
    void registerRequester_success() {
        // given
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(requesterRegistrationService.registerRequester(any())).thenReturn(testRequester);
        when(tokenService.generateToken()).thenReturn("test-token");
        when(userRepository.save(any())).thenReturn(testRequester);

        // when
        User result = userRegistrationService.registerUser(requesterRegisterDTO, null, null);

        // then
        assertNotNull(result);
        assertEquals("testrequester", result.getUsername());
        assertEquals("test-token", result.getToken());
        verify(userRepository).save(any());
    }

    @Test
    void registerUser_nullUserData() {
        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userRegistrationService.registerUser(null, null, null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void registerUser_missingAccountType() {
        // given
        driverRegisterDTO.setUserAccountType(null);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userRegistrationService.registerUser(driverRegisterDTO, null, null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void registerUser_duplicateUsername() {
        // given
        when(userRepository.existsByUsername(any())).thenReturn(true);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userRegistrationService.registerUser(driverRegisterDTO, null, null));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void registerUser_duplicateEmail() {
        // given
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userRegistrationService.registerUser(driverRegisterDTO, null, null));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void registerUser_duplicatePhoneNumber() {
        // given
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(true);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userRegistrationService.registerUser(driverRegisterDTO, null, null));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }
} 