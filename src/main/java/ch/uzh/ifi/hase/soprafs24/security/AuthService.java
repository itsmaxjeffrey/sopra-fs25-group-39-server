package ch.uzh.ifi.hase.soprafs24.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.CarRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.service.CarService;
import ch.uzh.ifi.hase.soprafs24.service.FileStorageService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RequesterRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.BaseUserRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DriverRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;

@Service
@Transactional
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private UserRepository userRepository;

    private final CarRepository carRepository;
    private final FileStorageService fileStorageService;
    private final CarService carService;
    private final LocationService locationService;


    public AuthService(
            UserRepository userRepository,
            CarRepository carRepository,
            FileStorageService fileStorageService,
            CarService carService,
            LocationService locationService) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.fileStorageService = fileStorageService;
        this.carService = carService;
        this.locationService = locationService;


    }

    /**
     * Register a new user (driver or requester) with file uploads
     * Returns the authenticated user after registration
     */
    public User registerUser(
            BaseUserRegisterDTO userRegisterDTO,
            @Nullable CarDTO carDTO,
            @Nullable LocationDTO locationDTO,
            @Nullable MultipartFile profilePicture,
            @Nullable MultipartFile driverLicense,
            @Nullable MultipartFile driverInsurance,
            @Nullable MultipartFile driverCarPicture) {
        
        // Validate input
        if (userRegisterDTO.getUserAccountType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "User account type must be specified");
        }
        
        // Check credential uniqueness
        checkUserCredentialUniqueness(userRegisterDTO);
        
        User newUser;
        switch (userRegisterDTO.getUserAccountType()) {
            case DRIVER -> {
                if (!(userRegisterDTO instanceof DriverRegisterDTO)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Driver registration requires DriverRegisterDTO");
                }
                    newUser = registerDriver(
                        (DriverRegisterDTO) userRegisterDTO,
                        carDTO,
                        locationDTO,
                        driverLicense,
                        driverInsurance,
                        driverCarPicture);
                         
            }
            case REQUESTER -> {
                if (!(userRegisterDTO instanceof RequesterRegisterDTO)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Requester registration requires RequesterRegisterDTO");
                }
                newUser = registerRequester((RequesterRegisterDTO) userRegisterDTO);
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
    
    //register driver helper
    

    //requester register helper






    
    /**
     * Check if username, email, phone number are unique
     */
    private void checkUserCredentialUniqueness(BaseUserRegisterDTO userToRegister) {
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