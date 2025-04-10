package ch.uzh.ifi.hase.soprafs24.security;

import java.util.HashMap;
import java.util.Map;

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
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register.UserRegistrationRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.UserDTOMapper;

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
    public ResponseEntity<Object> registerUser(@RequestBody UserRegistrationRequestDTO request) {
        
        //#DEBUG#
        System.out.println("Received user account type: " + 
        (request.getUser() != null ? request.getUser().getUserAccountType() : "NULL USER"));

        // Register and login the user with file uploads
        User authenticatedUser = userRegistrationService.registerUser(
            request.getUser(), 
            request.getCar(), 
            request.getLocation());
        
        // Create response map with user data including authentication token
        AuthenticatedUserDTO response = createAuthenticatedUserResponse(authenticatedUser);
        
        // Return created status with user data
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * JSON-only endpoint to register a user with pre-uploaded files
     * POST /api/v1/auth/register/json
     * User is automatically logged in after registration
     */
    @PostMapping("/register/json")
    public ResponseEntity<Object> registerUserJson(@RequestBody UserRegistrationRequestDTO request) {
        // Register the user with the JSON data
        User authenticatedUser = userRegistrationService.registerUser(
            request.getUser(), 
            request.getCar(), 
            request.getLocation());
        
        // Create response with user data including authentication token
        AuthenticatedUserDTO response = createAuthenticatedUserResponse(authenticatedUser);
        
        // Return created status with user data
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Debug endpoint to diagnose JSON parsing issues
     */
    @PostMapping("/register/debug")
    public ResponseEntity<Map<String, Object>> debugRegister(@RequestBody Map<String, Object> rawRequest) {
        Map<String, Object> debug = new HashMap<>();
        
        debug.put("rawRequest", rawRequest);
        debug.put("user", rawRequest.get("user"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> userMap = rawRequest.get("user") instanceof Map ? 
                                      (Map<String, Object>)rawRequest.get("user") : null;
        
        if (userMap != null) {
            debug.put("userAccountType", userMap.get("userAccountType"));
            debug.put("username", userMap.get("username"));
        }
        
        return ResponseEntity.ok(debug);
    }

    /**
     * Another debug endpoint that accepts and returns the exact same JSON
     */
    @PostMapping("/register/echo")
    public ResponseEntity<UserRegistrationRequestDTO> echoRegister(@RequestBody UserRegistrationRequestDTO request) {
        System.out.println("Echo received: " + 
            (request.getUser() != null ? 
                request.getUser().getClass().getSimpleName() + " with type " + 
                request.getUser().getUserAccountType() : "NULL USER"));
        
        return ResponseEntity.ok(request);
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