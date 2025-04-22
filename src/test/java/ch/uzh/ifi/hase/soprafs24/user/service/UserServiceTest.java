package ch.uzh.ifi.hase.soprafs24.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.BaseUserUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.DriverUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.RequesterUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserUpdateDTOMapper;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private UserUpdateDTOMapper userUpdateDTOMapper;

    @Mock
    private UserValidationService validationService;

    @Mock
    private DriverService driverService;

    @Mock
    private RequesterService requesterService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Driver testDriver;
    private Requester testRequester;
    private BaseUserUpdateDTO testUpdateDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setUserAccountType(UserAccountType.DRIVER);

        // Create test driver
        testDriver = new Driver();
        testDriver.setUserId(1L);
        testDriver.setUsername("testdriver");
        testDriver.setUserAccountType(UserAccountType.DRIVER);

        // Create test requester
        testRequester = new Requester();
        testRequester.setUserId(1L);
        testRequester.setUsername("testrequester");
        testRequester.setUserAccountType(UserAccountType.REQUESTER);

        // Create test update DTO
        testUpdateDTO = new BaseUserUpdateDTO();
        testUpdateDTO.setUsername("updateduser");
        testUpdateDTO.setUserAccountType(UserAccountType.DRIVER);
    }

    @Test
    void getUser_success() {
        // given
        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(testUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        User result = userService.getUser(1L, "valid-token");

        // then
        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void getUser_unauthorized() {
        // given
        when(authorizationService.authenticateUser(1L, "invalid-token")).thenReturn(null);

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            userService.getUser(1L, "invalid-token"));
    }

    @Test
    void getUser_notFound() {
        // given
        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(testUser);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            userService.getUser(1L, "valid-token"));
    }

    @Test
    void editUser_driver_success() {
        // given
        DriverUpdateDTO driverUpdateDTO = new DriverUpdateDTO();
        driverUpdateDTO.setUserAccountType(UserAccountType.DRIVER);
        
        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(testDriver);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        doNothing().when(validationService).validateEditPermission(1L, driverUpdateDTO);
        doNothing().when(validationService).validateUserAccountType(testDriver, driverUpdateDTO);
        doNothing().when(validationService).validateUniqueFields(driverUpdateDTO, testDriver);
        when(driverService.updateDriverDetails(testDriver, driverUpdateDTO)).thenReturn(testDriver);
        when(userRepository.save(testDriver)).thenReturn(testDriver);

        // when
        User result = userService.editUser(1L, "valid-token", driverUpdateDTO);

        // then
        assertNotNull(result);
        assertEquals(testDriver.getUserId(), result.getUserId());
        assertEquals(testDriver.getUsername(), result.getUsername());
    }

    @Test
    void editUser_requester_success() {
        // given
        RequesterUpdateDTO requesterUpdateDTO = new RequesterUpdateDTO();
        requesterUpdateDTO.setUserAccountType(UserAccountType.REQUESTER);
        
        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(testRequester);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testRequester));
        doNothing().when(validationService).validateEditPermission(1L, requesterUpdateDTO);
        doNothing().when(validationService).validateUserAccountType(testRequester, requesterUpdateDTO);
        doNothing().when(validationService).validateUniqueFields(requesterUpdateDTO, testRequester);
        when(requesterService.updateRequesterDetails(testRequester, requesterUpdateDTO)).thenReturn(testRequester);
        when(userRepository.save(testRequester)).thenReturn(testRequester);

        // when
        User result = userService.editUser(1L, "valid-token", requesterUpdateDTO);

        // then
        assertNotNull(result);
        assertEquals(testRequester.getUserId(), result.getUserId());
        assertEquals(testRequester.getUsername(), result.getUsername());
    }

    @Test
    void deleteUser_success() {
        // given
        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser); // Mock the save method

        // when
        userService.deleteUser(1L, "valid-token");

        // then
        // Verify that save is called instead of delete
        verify(userRepository).save(testUser); 
        // Optionally, verify that flush is also called
        verify(userRepository).flush(); 
        // Optionally, assert that user fields are anonymized
        assertEquals("deleted_user_1", testUser.getUsername());
        assertEquals("1@deleted.user", testUser.getEmail());
        assertEquals("", testUser.getPassword());
        assertNull(testUser.getToken());
        assertEquals("Deleted", testUser.getFirstName());
        assertEquals("User", testUser.getLastName());
        assertEquals("deleted_1", testUser.getPhoneNumber());
        assertNull(testUser.getBirthDate());
        assertNull(testUser.getProfilePicturePath());
    }

    @Test
    void deleteUser_unauthorized() {
        // given
        when(authorizationService.authenticateUser(1L, "invalid-token")).thenReturn(null);

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            userService.deleteUser(1L, "invalid-token"));
    }

    @Test
    void deleteUser_notFound() {
        // given
        when(authorizationService.authenticateUser(1L, "valid-token")).thenReturn(null);

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            userService.deleteUser(1L, "valid-token"));
    }
}