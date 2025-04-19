package ch.uzh.ifi.hase.soprafs24.security.authentication.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request.BaseUserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.security.registration.service.TokenService;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private BaseUserLoginDTO testLoginDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setToken("old-token");

        // Create test login DTO
        testLoginDTO = new BaseUserLoginDTO();
        testLoginDTO.setUsername("testuser");
        testLoginDTO.setPassword("password");
    }

    @Test
    void loginUser_success() {
        // given
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(testUser));
        when(tokenService.generateToken()).thenReturn("new-token");
        when(userRepository.save(any())).thenReturn(testUser);

        // when
        User result = authService.loginUser(testLoginDTO);

        // then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    void loginUser_userNotFound() {
        // given
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> authService.loginUser(testLoginDTO));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void loginUser_wrongPassword() {
        // given
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(testUser));
        testLoginDTO.setPassword("wrong-password");

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> authService.loginUser(testLoginDTO));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void logoutUser_success() {
        // given
        when(authorizationService.authenticateUser(any(), any())).thenReturn(testUser);
        when(userRepository.save(any())).thenReturn(testUser);

        // when
        authService.logoutUser(1L, "valid-token");

        // then
        verify(userRepository).save(any());
        assertNull(testUser.getToken());
    }

    @Test
    void logoutUser_invalidToken() {
        // given
        when(authorizationService.authenticateUser(any(), any())).thenReturn(null);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> authService.logoutUser(1L, "invalid-token"));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }
} 