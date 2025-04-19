package ch.uzh.ifi.hase.soprafs24.security.authentication.controller;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request.BaseUserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.security.registration.service.UserRegistrationService;


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


    // @Autowired
    private final UserDTOMapper userDTOMapper;

    public AuthController(AuthService authService, UserDTOMapper userDTOMapper, UserRegistrationService userRegistrationService) {
            this.authService = authService;
            this.userRegistrationService = userRegistrationService;
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
     * Helper method to create authenticated user response 
     * that includes authentication token and user account type
     */
    private AuthenticatedUserDTO createAuthenticatedUserResponse(User user) {
        return userDTOMapper.convertToDTO(user);
    }
}