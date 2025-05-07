package ch.uzh.ifi.hase.soprafs24.security.registration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.UserRegistrationRequestDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.service.UserRegistrationService;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserDTOMapper;

@RestController
@RequestMapping("/api/v1/auth")
public class RegistrationController {

    private final Logger log = LoggerFactory.getLogger(RegistrationController.class);
    private final UserDTOMapper userDTOMapper;
    private final UserRegistrationService userRegistrationService;

    public RegistrationController(UserRegistrationService userRegistrationService, UserDTOMapper userDTOMapper) {
        this.userDTOMapper = userDTOMapper;
        this.userRegistrationService = userRegistrationService;
    }
    

       /**
     * Single endpoint to register a new user (driver or requester) with file upload support
     * POST /api/v1/auth/register
     * User is automatically logged in after registration
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody UserRegistrationRequestDTO request) {
        // Validate request
        if (request == null || request.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User data is required");
        }
        
        log.debug("Received user account type: {}", 
            request.getUser() != null ? request.getUser().getUserAccountType() : "NULL USER");

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
     * Check if a username is available
     * GET /api/v1/auth/check-username/{username}
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Object> checkUsernameAvailability(@PathVariable String username) {
        boolean isTaken = userRegistrationService.checkUsernameAvailability(username);
        return ResponseEntity.ok(Map.of("isTaken", isTaken));
    }

    /**
     * Helper method to create authenticated user response 
     * that includes authentication token and user account type
     */
    private AuthenticatedUserDTO createAuthenticatedUserResponse(User user) {
        return userDTOMapper.convertToDTO(user);
    }

}
