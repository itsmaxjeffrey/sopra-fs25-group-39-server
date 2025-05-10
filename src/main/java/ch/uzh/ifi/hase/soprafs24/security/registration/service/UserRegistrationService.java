package ch.uzh.ifi.hase.soprafs24.security.registration.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.BaseUserRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.DriverRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.RequesterRegisterDTO;
@Service
public class UserRegistrationService {
    
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationService.class);
    
    private final UserRepository userRepository;
    private final DriverRegistrationService driverRegistrationService;
    private final RequesterRegistrationService requesterRegistrationService;
    private final TokenService tokenService;

    //initialize
    public UserRegistrationService(
    UserRepository userRepository,
    DriverRegistrationService driverRegistrationService,
    RequesterRegistrationService requesterRegistrationService,
    TokenService tokenService){
        this.userRepository = userRepository;
        this.driverRegistrationService = driverRegistrationService;
        this.requesterRegistrationService = requesterRegistrationService;
        this.tokenService = tokenService;
    }

    public boolean checkUsernameAvailability(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean checkEMailAdressAvailability(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkPhoneNumberAvailability(String phonenumber) {
        return userRepository.existsByPhoneNumber(phonenumber);
    }

    //handle register
    public User registerUser(
        BaseUserRegisterDTO baseUserRegisterDTO,
        @Nullable CarDTO carDTO,
        @Nullable LocationDTO locationDTO){

            
        if (baseUserRegisterDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User data is required");
        }
        
        if (baseUserRegisterDTO.getUserAccountType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "User account type is required (DRIVER or REQUESTER)");
        }

        checkUserCredentialUniqueness(baseUserRegisterDTO);

        User newUser;
        switch (baseUserRegisterDTO.getUserAccountType()) {
            case DRIVER -> {
                if (!(baseUserRegisterDTO instanceof DriverRegisterDTO)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Driver registration requires DriverRegisterDTO");
                }
                    newUser = driverRegistrationService.registerDriver(
                        (DriverRegisterDTO) baseUserRegisterDTO,
                        carDTO,
                        locationDTO);
                        
            }
            
            case REQUESTER -> {
                if (!(baseUserRegisterDTO instanceof RequesterRegisterDTO)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Requester registration requires RequesterRegisterDTO");
                }
                newUser = requesterRegistrationService.registerRequester();
            }

            //if user account type unknown
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid user account type");


        }
        

        
        // Set common user fields
        newUser.setUsername(baseUserRegisterDTO.getUsername());
        newUser.setPassword(baseUserRegisterDTO.getPassword());
        newUser.setEmail(baseUserRegisterDTO.getEmail());
        newUser.setFirstName(baseUserRegisterDTO.getFirstName());
        newUser.setLastName(baseUserRegisterDTO.getLastName());
        newUser.setPhoneNumber(baseUserRegisterDTO.getPhoneNumber());
        newUser.setBirthDate(baseUserRegisterDTO.getBirthDate());
        newUser.setUserAccountType(baseUserRegisterDTO.getUserAccountType());
        newUser.setWalletBalance(0.0);
        newUser.setProfilePicturePath(baseUserRegisterDTO.getProfilePicturePath());

        
        // Generate authentication token
        String token = tokenService.generateToken();
        newUser.setToken(token);
        
        // Save the user to repository
        newUser = userRepository.save(newUser);
        userRepository.flush();

        
        log.debug("Created new user: {}", newUser.getUsername());
        
        // User is automatically logged in after registration
        return newUser;
        

        }


    //helper to check uniqueness
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
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }
        
        
    }

