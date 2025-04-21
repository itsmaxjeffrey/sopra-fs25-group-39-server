package ch.uzh.ifi.hase.soprafs24.security.authentication.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request.BaseUserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request.PasswordChangeDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserDTOMapper;


/**
 * Auth Controller
 * This class handles all REST requests related to authentication
 * for both drivers and requesters using shared endpoints
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    // @Autowired
    private final UserDTOMapper userDTOMapper;

    public AuthController(AuthService authService, UserDTOMapper userDTOMapper) {
            this.authService = authService;
            this.userDTOMapper = userDTOMapper;
    }

    /**
     * Endpoint to login a user (driver or requester)
     * POST /api/v1/auth/login
     * System determines the account type based on username
     */
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody BaseUserLoginDTO baseUserLoginDTO) {
        // Login user
        User authenticatedUser = authService.loginUser(baseUserLoginDTO);
        
        // Create response map with user data including authentication token
        AuthenticatedUserDTO response = createAuthenticatedUserResponse(authenticatedUser);
        
        // Return OK status with user data
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Endpoint to logout a user (invalidate their token)
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logoutUser(@RequestHeader("UserId") Long userId, @RequestHeader("Authorization") String token) {
        authService.logoutUser(userId, token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully logged out");
        response.put("timestamp", System.currentTimeMillis());


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint to change a user's password
     * PUT /api/v1/auth/change-password
     */
    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordChangeDTO passwordChangeDTO) {
        try {
            authService.changePassword(userId, token, passwordChangeDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getReason());
            response.put("timestamp", System.currentTimeMillis());
            return new ResponseEntity<>(response, e.getStatus());
        } catch (Exception e) {
            // Generic error handling
            Map<String, Object> response = new HashMap<>();
            response.put("message", "An unexpected error occurred.");
            response.put("timestamp", System.currentTimeMillis());
            LOGGER.error("Unexpected error during password change for user {}", userId, e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Helper method to create authenticated user response 
     * that includes authentication token and user account type
     */
    private AuthenticatedUserDTO createAuthenticatedUserResponse(User user) {
        return userDTOMapper.convertToDTO(user);
    }
}