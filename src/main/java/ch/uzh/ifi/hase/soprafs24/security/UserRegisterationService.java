package ch.uzh.ifi.hase.soprafs24.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.BaseUserRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DriverRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RequesterRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.service.FileStorageService;
@Service
public class UserRegisterationService {
    
    private static final Logger log = LoggerFactory.getLogger(UserRegisterationService.class);
    
    private final UserRepository userRepository;
    private final DriverRegisterationService driverRegisterationService;
    private final RequesterRegisterationService requesterRegisterationService;
    private final FileStorageService fileStorageService;
    private final TokenService tokenService;

    //initialize
    public UserRegisterationService(
    UserRepository userRepository,
    DriverRegisterationService driverRegisterationService,
    RequesterRegisterationService requesterRegisterationService,
    FileStorageService fileStorageService,
    TokenService tokenService){
        this.userRepository = userRepository;
        this.driverRegisterationService = driverRegisterationService;
        this.requesterRegisterationService = requesterRegisterationService;
        this.fileStorageService = fileStorageService;
        this.tokenService = tokenService;
    }

    //handle register
    public User registerUser(
        BaseUserRegisterDTO baseUserRegisterDTO,
        @Nullable CarDTO carDTO,
        @Nullable LocationDTO locationDTO,
        @Nullable MultipartFile profilePicture,
        @Nullable MultipartFile driverLicense,
        @Nullable MultipartFile driverInsurance,
        @Nullable MultipartFile driverCarPicture){


        User newUser;
        switch (baseUserRegisterDTO.getUserAccountType()) {
            case DRIVER -> {
                if (!(baseUserRegisterDTO instanceof DriverRegisterDTO)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Driver registration requires DriverRegisterDTO");
                }
                    newUser = driverRegisterationService.registerDriver(
                        (DriverRegisterDTO) baseUserRegisterDTO,
                        carDTO,
                        locationDTO,
                        driverLicense,
                        driverInsurance,
                        driverCarPicture);
                        
            }
            
            case REQUESTER -> {
                if (!(baseUserRegisterDTO instanceof RequesterRegisterDTO)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Requester registration requires RequesterRegisterDTO");
                }
                newUser = requesterRegisterationService.registerRequester((RequesterRegisterDTO) baseUserRegisterDTO);
            }

            //if user account type unknown
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid user account type");


        }
        
        // Process profile picture if provided
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String profilePicturePath = fileStorageService.storeFile(profilePicture, "profile-pictures");
            newUser.setProfilePicturePath(profilePicturePath);
        }
        
        // Set common user fields
        newUser.setUsername(baseUserRegisterDTO.getUsername());
        newUser.setPassword(baseUserRegisterDTO.getPassword()); // In a real app, encrypt this!
        newUser.setEmail(baseUserRegisterDTO.getEmail());
        newUser.setFirstName(baseUserRegisterDTO.getFirstName());
        newUser.setLastName(baseUserRegisterDTO.getLastName());
        newUser.setPhoneNumber(baseUserRegisterDTO.getPhoneNumber());
        newUser.setUserBio(baseUserRegisterDTO.getUserBio());
        newUser.setBirthDate(baseUserRegisterDTO.getBirthDate());
        newUser.setUserAccountType(baseUserRegisterDTO.getUserAccountType());
        newUser.setWalletBalance(0.0);
        
        // Generate authentication token
        String token = tokenService.generateToken();
        newUser.setToken(token);
        
        // Save the user to repository
        newUser = userRepository.save(newUser);
        
        log.debug("Created new user: {}", newUser.getUsername());
        
        // User is automatically logged in after registration
        return newUser;
        

        }


        
    }
    
