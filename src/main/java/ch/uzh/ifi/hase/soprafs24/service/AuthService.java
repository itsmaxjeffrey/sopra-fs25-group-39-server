package ch.uzh.ifi.hase.soprafs24.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserRegisterDTO;

@Service
@Transactional
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            CarRepository carRepository,
            FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Register a new user (driver or requester) with file uploads
     * Returns the authenticated user after registration
     */
    public User registerUser(
            UserRegisterDTO userRegisterDTO,
            MultipartFile profilePicture,
            MultipartFile driverLicense,
            MultipartFile driverInsurance) {
        
        // Validate input
        if (userRegisterDTO.getUserAccountType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "User account type must be specified");
        }
        
        // Check credential uniqueness
        checkUserCredentialUniqueness(userRegisterDTO);
        
        User newUser;
        
        if (null == userRegisterDTO.getUserAccountType()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid user account type");
        } 
        else // Create appropriate user type
            switch (userRegisterDTO.getUserAccountType()) {
                case DRIVER -> {
                    // Validate driver-specific requirements
                    if (driverLicense == null || driverLicense.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Driver license is required for driver registration");
                    }   if (userRegisterDTO.getCarId() == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Car is required for driver registration");
                    }   Driver driver = new Driver();
                    // Process driver license (required)
                    String driverLicensePath = fileStorageService.storeFile(driverLicense, "driver-licenses");
                    driver.setDriverLicensePath(driverLicensePath);
                    // Process driver insurance if provided
                    if (driverInsurance != null && !driverInsurance.isEmpty()) {
                        String driverInsurancePath = fileStorageService.storeFile(driverInsurance, "driver-insurances");
                        driver.setDriverInsurancePath(driverInsurancePath);
                    }   // Set driver-specific fields
                    driver.setPreferredRange(userRegisterDTO.getPreferredRange());
                    // Set car if provided
                    if (userRegisterDTO.getCarId() != null) {
                        Optional<Car> car = carRepository.findById(userRegisterDTO.getCarId());
                        if (car.isEmpty()) {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found");
                        }
                        driver.setCar(car.get());
                    }   newUser = driver;
                }
                case REQUESTER -> {
                    Requester requester = new Requester();
                    newUser = requester;
                }
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid user account type");
            }
        
        // Process profile picture if provided
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String profilePicturePath = fileStorageService.storeFile(profilePicture, "profile-pictures");
            newUser.setProfilePicturePath(profilePicturePath);
        }
        
        // Set common user fields
        newUser.setUsername(userRegisterDTO.getUsername());
        newUser.setPassword(userRegisterDTO.getPassword()); // In a real app, encrypt this!
        newUser.setEmail(userRegisterDTO.getEmail());
        newUser.setFirstName(userRegisterDTO.getFirstName());
        newUser.setLastName(userRegisterDTO.getLastName());
        newUser.setPhoneNumber(userRegisterDTO.getPhoneNumber());
        newUser.setUserBio(userRegisterDTO.getUserBio());
        newUser.setBirthDate(userRegisterDTO.getBirthDate());
        newUser.setUserAccountType(userRegisterDTO.getUserAccountType());
        newUser.setWalletBalance(0.0);
        
        // Generate authentication token
        String token = generateToken();
        newUser.setToken(token);
        
        // Save the user to repository
        newUser = userRepository.save(newUser);
        
        log.debug("Created new user: {}", newUser.getUsername());
        
        // User is automatically logged in after registration
        return newUser;
    }

    /**
     * Login a user with username and password
     * The system determines the account type
     */
    public User loginUser(UserLoginDTO userLoginDTO) {
        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(userLoginDTO.getUsername());
            
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "User not found");
        }
        
        User user = userOptional.get();
        
        // Check password (in a real app, use proper password encryption)
        if (!user.getPassword().equals(userLoginDTO.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                "Password is incorrect");
        }
        
        // Generate new token for this session
        String token = generateToken();
        user.setToken(token);
        
        // Save updated user with new token
        user = userRepository.save(user);
        
        log.debug("User logged in: {}", user.getUsername());
        return user;
    }
    
    /**
     * Logout a user by invalidating their token
     */
    public void logoutUser(String token) {
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required");
        }
        
        User user = getUserByToken(token);
        user.setToken(null);
        userRepository.save(user);
        
        log.debug("User logged out: {}", user.getUsername());
    }
    
    /**
     * Validate a user's token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        return userRepository.existsByToken(token);
    }
    
    /**
     * Validate if a token belongs to a specific user
     * @return true if valid for this user, false otherwise
     */
    public boolean validateTokenForUser(Long userId, String token) {
        if (token == null || token.isEmpty() || userId == null) {
            return false;
        }
        
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        return token.equals(user.getToken());
    }
    
    /**
     * Get user by token
     */
    public User getUserByToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        
        Optional<User> userOptional = userRepository.findByToken(token);
        
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        
        return userOptional.get();
    }
    
    /**
     * Generate a unique token for authentication
     */
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Check if username, email, phone number are unique
     */
    private void checkUserCredentialUniqueness(UserRegisterDTO userToRegister) {
        List<String> notUniqueAttributes = new ArrayList<>();

        if (userRepository.existsByUsername(userToRegister.getUsername())) {
            notUniqueAttributes.add("Username");
        }
        
        if (userRepository.existsByEmail(userToRegister.getEmail())) {
            notUniqueAttributes.add("Email");
        }
        
        if (userRepository.existsByPhoneNumber(userToRegister.getPhoneNumber())) {
            notUniqueAttributes.add("Phone Number");
        }

        if (!notUniqueAttributes.isEmpty()) {
            String errorMessage = String.format(
                "The %s provided %s not unique. Therefore, the account could not be created!",
                String.join(", ", notUniqueAttributes),
                notUniqueAttributes.size() > 1 ? "are" : "is"
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }
}