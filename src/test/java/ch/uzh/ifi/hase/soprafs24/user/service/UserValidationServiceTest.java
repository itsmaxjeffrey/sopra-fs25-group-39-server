package ch.uzh.ifi.hase.soprafs24.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.BaseUserUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.DriverUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.RequesterUpdateDTO;

class UserValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidationService userValidationService;

    private User testUser;
    private BaseUserUpdateDTO testUpdateDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPhoneNumber("1234567890");
        testUser.setUserAccountType(UserAccountType.DRIVER);

        // Create test update DTO
        testUpdateDTO = new BaseUserUpdateDTO();
        testUpdateDTO.setUsername("updateduser");
        testUpdateDTO.setEmail("updated@example.com");
        testUpdateDTO.setPhoneNumber("0987654321");
        testUpdateDTO.setUserAccountType(UserAccountType.DRIVER);
    }

    @Test
    void validateUniqueFields_noConflicts_success() {
        // given
        when(userRepository.existsByUsernameAndUserIdNot(any(), any())).thenReturn(false);
        when(userRepository.existsByEmailAndUserIdNot(any(), any())).thenReturn(false);
        when(userRepository.existsByPhoneNumberAndUserIdNot(any(), any())).thenReturn(false);

        // when/then
        assertDoesNotThrow(() -> 
            userValidationService.validateUniqueFields(testUpdateDTO, testUser));
    }

    @Test
    void validateUniqueFields_duplicateUsername() {
        // given
        when(userRepository.existsByUsernameAndUserIdNot(any(), any())).thenReturn(true);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userValidationService.validateUniqueFields(testUpdateDTO, testUser));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Username is already taken", exception.getReason());
    }

    @Test
    void validateUniqueFields_duplicateEmail() {
        // given
        when(userRepository.existsByUsernameAndUserIdNot(any(), any())).thenReturn(false);
        when(userRepository.existsByEmailAndUserIdNot(any(), any())).thenReturn(true);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userValidationService.validateUniqueFields(testUpdateDTO, testUser));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Email is already taken", exception.getReason());
    }

    @Test
    void validateUniqueFields_duplicatePhoneNumber() {
        // given
        when(userRepository.existsByUsernameAndUserIdNot(any(), any())).thenReturn(false);
        when(userRepository.existsByEmailAndUserIdNot(any(), any())).thenReturn(false);
        when(userRepository.existsByPhoneNumberAndUserIdNot(any(), any())).thenReturn(true);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userValidationService.validateUniqueFields(testUpdateDTO, testUser));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Phone number is already taken", exception.getReason());
    }

    @Test
    void validateUserAccountType_sameType_success() {
        // given
        DriverUpdateDTO driverUpdateDTO = new DriverUpdateDTO();
        driverUpdateDTO.setUserAccountType(UserAccountType.DRIVER);

        // when/then
        assertDoesNotThrow(() -> 
            userValidationService.validateUserAccountType(testUser, driverUpdateDTO));
    }

    @Test
    void validateUserAccountType_differentType() {
        // given
        RequesterUpdateDTO requesterUpdateDTO = new RequesterUpdateDTO();
        requesterUpdateDTO.setUserAccountType(UserAccountType.REQUESTER);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userValidationService.validateUserAccountType(testUser, requesterUpdateDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getReason().contains("Cannot change user account type"));
    }

    @Test
    void validateEditPermission_sameUser_success() {
        // given
        testUpdateDTO.setUserId(1L);

        // when/then
        assertDoesNotThrow(() -> 
            userValidationService.validateEditPermission(1L, testUpdateDTO));
    }

    @Test
    void validateEditPermission_differentUser() {
        // given
        testUpdateDTO.setUserId(2L);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            userValidationService.validateEditPermission(1L, testUpdateDTO));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You can only edit your own profile", exception.getReason());
    }
} 