package ch.uzh.ifi.hase.soprafs24.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.exceptions.UserNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.authorization.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.service.CarService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;

@Service
public class UserService {

    private final AuthorizationService authorizationService;
    private final UserRepository userRepository;
    private final UserUpdateDTOMapper userUpdateDTOMapper;
    private final CarService carService;
    private final LocationService locationService;

    public UserService(UserRepository userRepository, 
                      AuthorizationService authorizationService,
                      UserUpdateDTOMapper userUpdateDTOMapper,
                      CarService carService,
                      LocationService locationService) {
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
        this.userUpdateDTOMapper = userUpdateDTOMapper;
        this.carService = carService;
        this.locationService = locationService;
    }
    
    public User getUser(Long userId, String token) {
        // First authenticate the requesting user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
        }
        // Then get the requested user (could be different from the authenticated user)
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }



    
    public User editUser(Long userId, String token, BaseUserUpdateDTO userUpdateDTO) {
        // First authenticate the requesting user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
        }
        
        // Users can only edit their own profiles
        if (!userId.equals(userUpdateDTO.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own profile");
        }
        
        // Get existing user from the repository
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Check if user types match
        if (!existingUser.getUserAccountType().equals(userUpdateDTO.getUserAccountType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot change user account type from " + existingUser.getUserAccountType() + 
                " to " + userUpdateDTO.getUserAccountType());
        }
        
        // Check for unique fields that might be violated
        validateUniqueFields(userUpdateDTO, existingUser);
        
        // Apply updates based on user type using our polymorphic mapper approach
        User updatedUser;
        
        if (existingUser instanceof Driver && userUpdateDTO instanceof DriverUpdateDTO) {
            // Convert DTO to entity for basic fields
            Driver driverDataFromDTO = (Driver) userUpdateDTOMapper.convertToEntity(userUpdateDTO);
            
            // Update the existing entity with data from DTO
            updatedUser = updateDriverFields((Driver) existingUser, driverDataFromDTO, (DriverUpdateDTO) userUpdateDTO);
        } 
        else if (existingUser instanceof Requester && userUpdateDTO instanceof RequesterUpdateDTO) {
            // Convert DTO to entity for basic fields
            Requester requesterDataFromDTO = (Requester) userUpdateDTOMapper.convertToEntity(userUpdateDTO);
            
            // Update the existing entity with data from DTO
            updatedUser = updateRequesterFields((Requester) existingUser, requesterDataFromDTO, (RequesterUpdateDTO) userUpdateDTO);
        }
        else {
            // Just update common fields for base User type
            updatedUser = updateCommonFields(existingUser, userUpdateDTO);
        }
        
        // Save and return the updated user
        updatedUser = userRepository.save(updatedUser);
        userRepository.flush();
        
        return updatedUser;
    }


    //helper method for edit user to check uniquenss
    private void validateUniqueFields(BaseUserUpdateDTO updates, User existingUser) {
        // Check username uniqueness
        if (updates.getUsername() != null && !updates.getUsername().isEmpty() && 
            !updates.getUsername().equals(existingUser.getUsername())) {
            
            // This should check if any OTHER user has this username
            if (userRepository.existsByUsernameAndUserIdNot(updates.getUsername(), existingUser.getUserId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
            }
        }
    
        // Check email uniqueness  
        if (updates.getEmail() != null && !updates.getEmail().isEmpty() && 
            !updates.getEmail().equals(existingUser.getEmail())) {
            
            // This should check if any OTHER user has this email
            if (userRepository.existsByEmailAndUserIdNot(updates.getEmail(), existingUser.getUserId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
            }
        }
        
        // Check phone number uniqueness
        if (updates.getPhoneNumber() != null && !updates.getPhoneNumber().isEmpty() && 
            !updates.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
            
            // This should check if any OTHER user has this phone number
            if (userRepository.existsByPhoneNumberAndUserIdNot(updates.getPhoneNumber(), existingUser.getUserId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number is already taken");
            }
        }

        
    }



    // Add these helper methods for the editUser method

private User updateCommonFields(User existingUser, BaseUserUpdateDTO updates) {
    if (updates.getUsername() != null && !updates.getUsername().isEmpty()) {
        existingUser.setUsername(updates.getUsername());
    }
    
    if (updates.getEmail() != null && !updates.getEmail().isEmpty()) {
        existingUser.setEmail(updates.getEmail());
    }
    
    if (updates.getPhoneNumber() != null && !updates.getPhoneNumber().isEmpty()) {
        existingUser.setPhoneNumber(updates.getPhoneNumber());
    }
    
    if (updates.getFirstName() != null) {
        existingUser.setFirstName(updates.getFirstName());
    }
    
    if (updates.getLastName() != null) {
        existingUser.setLastName(updates.getLastName());
    }
    
    if (updates.getUserBio() != null) {
        existingUser.setUserBio(updates.getUserBio());
    }
    
    if (updates.getBirthDate() != null) {
        existingUser.setBirthDate(updates.getBirthDate());
    }
    
    if (updates.getProfilePicturePath() != null) {
        existingUser.setProfilePicturePath(updates.getProfilePicturePath());
    }
    
    return existingUser;
}

private Driver updateDriverFields(Driver driver, Driver driverDataFromDTO, DriverUpdateDTO updates) {
    // Update common fields
    updateCommonFields(driver, updates);
    
    // Update driver-specific fields
    if (updates.getDriverLicensePath() != null) {
        driver.setDriverLicensePath(updates.getDriverLicensePath());
    }
    
    if (updates.getDriverInsurancePath() != null) {
        driver.setDriverInsurancePath(updates.getDriverInsurancePath());
    }
    
    if (updates.getPreferredRange() > 0) {
        driver.setPreferredRange(updates.getPreferredRange());
    }
    
    // Handle car updates if provided
    if (updates.getCar() != null) {
        Car car = driver.getCar();
        if (car == null) {
            car = new Car();
        }
        
        if (updates.getCar().getCarModel() != null) {
            car.setCarModel(updates.getCar().getCarModel());
        }
        
        if (updates.getCar().getLicensePlate() != null) {
            car.setLicensePlate(updates.getCar().getLicensePlate());
        }
        
        if (updates.getCar().getCarPicturePath() != null) {
            car.setCarPicturePath(updates.getCar().getCarPicturePath());
        }
        
        if (updates.getCar().getSpace() > 0) {
            car.setSpace(updates.getCar().getSpace());
        }
        
        if (updates.getCar().getSupportedWeight() > 0) {
            car.setSupportedWeight(updates.getCar().getSupportedWeight());
        }
        
        car.setElectric(updates.getCar().isElectric());
        
        car = carService.createCar(car);
        driver.setCar(car);
    }
    
    // Handle location updates if provided
    if (updates.getLocation() != null) {
        Location location = driver.getLocation();
        if (location == null) {
            location = new Location();
        }
        
        location.setLatitude(updates.getLocation().getLatitude());
        location.setLongitude(updates.getLocation().getLongitude());
        location.setFormattedAddress(updates.getLocation().getFormattedAddress());
        
        location = locationService.createLocation(location);
        driver.setLocation(location);
    }
    
    return driver;
}

private Requester updateRequesterFields(Requester requester, Requester requesterDataFromDTO, RequesterUpdateDTO updates) {
    // Update common fields
    updateCommonFields(requester, updates);
    
    // Add requester-specific field updates here if needed in the future
    
    return requester;
}
}
