package ch.uzh.ifi.hase.soprafs24.security.registration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register.UserRegistrationRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedUserDTO;

@RestController
@RequestMapping("/api/v1/auth")
public class RegistrationController {
    private final UserRegistrationService userRegistrationService;

    public RegistrationController(UserRegistrationService userRegistrationService) {
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

}
