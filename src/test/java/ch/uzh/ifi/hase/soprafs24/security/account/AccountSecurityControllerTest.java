package ch.uzh.ifi.hase.soprafs24.security.account;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.account.controller.AccountSecurityController;
import ch.uzh.ifi.hase.soprafs24.security.account.dto.request.UserDeleteRequestDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountSecurityControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private AuthorizationService authorizationService;
    @InjectMocks
    private AccountSecurityController accountSecurityController;

    private User testUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    void deleteAccountVerified_success() {
        when(authorizationService.authenticateUser(1L, "token")).thenReturn(testUser);
        UserDeleteRequestDTO dto = new UserDeleteRequestDTO();
        dto.setEmail("test@example.com");
        ResponseEntity<Object> response = accountSecurityController.deleteAccountVerified(1L, "token", dto);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L, "token");
    }

    @Test
    void deleteAccountVerified_unauthorized() {
        when(authorizationService.authenticateUser(1L, "token")).thenReturn(null);
        UserDeleteRequestDTO dto = new UserDeleteRequestDTO();
        dto.setEmail("test@example.com");
        ResponseEntity<Object> response = accountSecurityController.deleteAccountVerified(1L, "token", dto);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid credentials"));
    }

    @Test
    void deleteAccountVerified_forbidden_wrongEmail() {
        when(authorizationService.authenticateUser(1L, "token")).thenReturn(testUser);
        UserDeleteRequestDTO dto = new UserDeleteRequestDTO();
        dto.setEmail("wrong@example.com");
        ResponseEntity<Object> response = accountSecurityController.deleteAccountVerified(1L, "token", dto);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("does not match account email"));
    }
} 