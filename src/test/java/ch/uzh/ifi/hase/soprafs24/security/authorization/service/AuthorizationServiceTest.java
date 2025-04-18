package ch.uzh.ifi.hase.soprafs24.security.authorization.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

public class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setToken("valid-token");
        testUser.setUserAccountType(UserAccountType.DRIVER);
    }

    @Test
    public void authenticateUser_success() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

        // when
        User result = authorizationService.authenticateUser(1L, "valid-token");

        // then
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    public void authenticateUser_nullToken() {
        // when
        User result = authorizationService.authenticateUser(1L, null);

        // then
        assertNull(result);
    }

    @Test
    public void authenticateUser_emptyToken() {
        // when
        User result = authorizationService.authenticateUser(1L, "");

        // then
        assertNull(result);
    }

    @Test
    public void authenticateUser_userNotFound() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when
        User result = authorizationService.authenticateUser(1L, "valid-token");

        // then
        assertNull(result);
    }

    @Test
    public void authenticateUser_invalidToken() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

        // when
        User result = authorizationService.authenticateUser(1L, "invalid-token");

        // then
        assertNull(result);
    }

    @Test
    public void authorizeUser_success() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

        // when
        boolean result = authorizationService.authorizeUser(1L, "valid-token", UserAccountType.DRIVER);

        // then
        assertTrue(result);
    }

    @Test
    public void authorizeUser_wrongAccountType() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

        // when
        boolean result = authorizationService.authorizeUser(1L, "valid-token", UserAccountType.REQUESTER);

        // then
        assertFalse(result);
    }

    @Test
    public void authorizeUser_invalidToken() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

        // when
        boolean result = authorizationService.authorizeUser(1L, "invalid-token", UserAccountType.DRIVER);

        // then
        assertFalse(result);
    }
} 