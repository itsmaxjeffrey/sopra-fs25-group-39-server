package ch.uzh.ifi.hase.soprafs24.security.authentication;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.login.BaseUserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.security.registration.UserRegistrationService;
import ch.uzh.ifi.hase.soprafs24.security.registration.UserRegistrationService;

/**
 * Auth Controller
 * This class handles all REST requests related to authentication
 * for both drivers and requesters using shared endpoints
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRegistrationService userRegistrationService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private UserDTOMapper userDTOMapper;

    public AuthController(
        AuthService authService,
        UserRegistrationService userRegistrationService) {
            this.authService = authService;
            this.userRegistrationService = userRegistrationService;
            
    }

    /**
     * Single endpoint to register a new user (driver or requester) with file upload support
     * POST /api/v1/auth/register
     * User is automatically logged in after registration
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody BaseUserRegisterDTO baseUserRegisterDTO) {
        logger.debug("Received registration request: {}", baseUserRegisterDTO);
    
        try {
            // Register and login the user
            User authenticatedUser = userRegistrationService.registerUser(
                baseUserRegisterDTO,
                null, // No car data
                null, // No location data
                null, // No profile picture
                null, // No driver license
                null, // No driver insurance
                null  // No driver car picture
            );
    
            // Create response map with user data including authentication token
            AuthenticatedUserDTO response = createAuthenticatedUserResponse(authenticatedUser);
    
            // Log the response
            logger.debug("Returning response: {}", response);
    
            // Return created status with user data
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage(), e);
    
            // Return an error response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Registration failed");
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to login a user (driver or requester)
     * POST /api/v1/auth/login
     * System determines the account type based on username
     */
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody BaseUserLoginDTO BaseUserLoginDTO) {
        // Login user
        User authenticatedUser = authService.loginUser(BaseUserLoginDTO);
        
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
    public ResponseEntity<Object> logoutUser(@RequestHeader("Authorization") String token) {
        authService.logoutUser(token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully logged out");
        response.put("timestamp", System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Helper method to create authenticated user response 
     * that includes authentication token and user account type
     */
    private AuthenticatedUserDTO createAuthenticatedUserResponse(User user) {
        return userDTOMapper.convertToDTO(user);
    }
}