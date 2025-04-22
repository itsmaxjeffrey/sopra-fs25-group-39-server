package ch.uzh.ifi.hase.soprafs24.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.BaseUserUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.DriverUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.RequesterUpdateDTO;

/**
 * Main service for user operations
 * Acts as a facade to coordinate user-related operations
 */
@Service
public class UserService extends AbstractUserService {

    private final UserValidationService validationService;
    private final DriverService driverService;
    private final RequesterService requesterService;

    public UserService(
            UserRepository userRepository, 
            AuthorizationService authorizationService,
            UserValidationService validationService,
            DriverService driverService,
            RequesterService requesterService) {
        super(userRepository, authorizationService);
        this.validationService = validationService;
        this.driverService = driverService;
        this.requesterService = requesterService;
    }
    
    /**
     * Gets a user by ID
     */
    public User getUser(Long userId, String token) {
        // First authenticate the requesting user
        authenticateRequest(userId, token);
        
        // Then get the requested user
        return findUserById(userId);
    }
    
    /**
     * Edits a user's profile
     */
    public User editUser(Long userId, String token, BaseUserUpdateDTO userUpdateDTO) {
        // First authenticate the requesting user
        authenticateRequest(userId, token);
        
        // Check if user has permission to edit this profile
        validationService.validateEditPermission(userId, userUpdateDTO);
        
        // Get existing user from the repository
        User existingUser = findUserById(userId);
        
        // Check if user types match
        validationService.validateUserAccountType(existingUser, userUpdateDTO);
        
        // Check for unique fields that might be violated
        validationService.validateUniqueFields(userUpdateDTO, existingUser);
        
        // Apply updates based on user type
        User updatedUser = updateUserByType(existingUser, userUpdateDTO);
        
        // Save and return the updated user
        return saveUser(updatedUser);

    }
    
    /**
     * Dispatches update to the appropriate service based on user type
     */
    private User updateUserByType(User existingUser, BaseUserUpdateDTO userUpdateDTO) {
        if (existingUser instanceof Driver driver && userUpdateDTO instanceof DriverUpdateDTO driverUpdateDTO) {
            return driverService.updateDriverDetails(driver, driverUpdateDTO);
        } 
        else if (existingUser instanceof Requester requester && userUpdateDTO instanceof RequesterUpdateDTO requesterUpdateDTO) {
            return requesterService.updateRequesterDetails(requester, requesterUpdateDTO);
        }
        else {
            // Just update common fields for base User type
            return updateCommonFields(existingUser, userUpdateDTO);
        }
    }



    /**
     * Anonymizes a user's account instead of deleting it.
     * Sets personal information to null or placeholder values.
     * @param userId ID of the user requesting anonymization
     * @param token Authentication token
     * @return void
     */
    public void deleteUser(Long userId, String token) {
        // First authenticate the requesting user
        User user = authenticateRequest(userId, token);
        
        // User can only anonymize their own account
        if (!user.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only anonymize your own account");
        }
        
        // Anonymize user data
        user.setUsername("deleted_user_" + user.getUserId());
        user.setEmail(user.getUserId() + "@deleted.user"); // Unique placeholder
        user.setPassword(""); // Clear password, non-null
        user.setToken(null); // Invalidate token
        user.setFirstName("Deleted"); // Placeholder for non-null field
        user.setLastName("User"); // Placeholder for non-null field
        user.setPhoneNumber("deleted_" + user.getUserId()); // Unique placeholder for non-null, unique field
        user.setBirthDate(null); 
        user.setProfilePicturePath(null);
        // Consider if Driver/Requester specific fields need anonymization
        // e.g., driverLicensePath, driverInsurancePath for Driver

        // Save the anonymized user
        userRepository.save(user);
        userRepository.flush();
    }

        /**
     * Gets a user by ID after authenticating the requesting user
     * @param requestingUserId ID of the user making the request
     * @param token Authentication token
     * @param targetUserId ID of the user being requested
     * @return The requested user if found
     */
    public User getUserById(Long requestingUserId, String token, Long targetUserId) {
        // First authenticate the requesting user
        authenticateRequest(requestingUserId, token);
        
        // Then return the requested user
        return findUserById(targetUserId);
    }
}

