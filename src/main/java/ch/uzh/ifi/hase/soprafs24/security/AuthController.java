package ch.uzh.ifi.hase.soprafs24.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.login.BaseUserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register.BaseUserRegisterDTO;

/**
 * Auth Controller
 * This class handles all REST requests related to authentication
 * for both drivers and requesters using shared endpoints
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRegisterationService userRegisterationService;

    public AuthController(
        AuthService authService,
        UserRegisterationService userRegisterationService) {
            this.authService = authService;
            this.userRegisterationService = userRegisterationService;
    }

    /**
     * Single endpoint to register a new user (driver or requester) with file upload support
     * POST /api/v1/auth/register
     * User is automatically logged in after registration
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(
            @RequestPart(name="baseUserRegisterData",required=false) BaseUserRegisterDTO baseUserRegisterDTO,
            @RequestPart(name="carData",required=false) CarDTO carDTO,
            @RequestPart(name="locationData",required=false) LocationDTO locationDTO,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestPart(value = "driverLicense", required = false) MultipartFile driverLicense,
            @RequestPart(value = "driverInsurance", required = false) MultipartFile driverInsurance,
            @RequestPart(value = "driverCarPicture", required = false) MultipartFile driverCarPicture) {
        
        // Register and login the user with file uploads
        User authenticatedUser = userRegisterationService.registerUser(
            baseUserRegisterDTO,
            carDTO,
            locationDTO,
            profilePicture,
            driverLicense,
            driverInsurance,
            driverCarPicture);
        
        // Create response map with user data including authentication token
        Map<String, Object> response = createAuthenticatedUserResponse(authenticatedUser);
        
        // Return created status with user data
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
        Map<String, Object> response = createAuthenticatedUserResponse(authenticatedUser);
        
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
    private Map<String, Object> createAuthenticatedUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        
        // Add authentication details
        response.put("token", user.getToken());
        response.put("userId", user.getUserId());
        response.put("userAccountType", user.getUserAccountType());
        
        // Add common user fields
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("walletBalance", user.getWalletBalance());
        response.put("birthDate", user.getBirthDate());
        response.put("userBio", user.getUserBio());
        response.put("profilePicturePath", user.getProfilePicturePath());
        
        // Add user type specific fields
        if (user instanceof Driver) {
            Driver driver = (Driver) user;
            response.put("driverLicensePath", driver.getDriverLicensePath());
            response.put("driverInsurancePath", driver.getDriverInsurancePath());
            response.put("preferredRange", driver.getPreferredRange());
            
            if (driver.getCar() != null) {
                Map<String, Object> carInfo = new HashMap<>();
                carInfo.put("carId", driver.getCar().getCarId());
                // Add other car details you might want to include
                response.put("car", carInfo);
            }
            
            if (driver.getLocation() != null) {
                Map<String, Object> locationInfo = new HashMap<>();
                // Add location details
                response.put("location", locationInfo);
            }
        }
        // You can add Requester-specific fields here if needed
        
        return response;
    }
}