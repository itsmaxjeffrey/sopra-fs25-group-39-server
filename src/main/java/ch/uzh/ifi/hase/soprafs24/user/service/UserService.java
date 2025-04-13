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
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserUpdateDTOMapper;

/**
 * Main service for user operations
 * Acts as a facade to coordinate user-related operations
 */
@Service
public class UserService extends AbstractUserService {

    private final UserUpdateDTOMapper userUpdateDTOMapper;
    private final UserValidationService validationService;
    private final DriverService driverService;
    private final RequesterService requesterService;

    public UserService(
            UserRepository userRepository, 
            AuthorizationService authorizationService,
            UserUpdateDTOMapper userUpdateDTOMapper,
            UserValidationService validationService,
            DriverService driverService,
            RequesterService requesterService) {
        super(userRepository, authorizationService);
        this.userUpdateDTOMapper = userUpdateDTOMapper;
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
        if (existingUser instanceof Driver && userUpdateDTO instanceof DriverUpdateDTO) {
            return driverService.updateDriverDetails((Driver) existingUser, (DriverUpdateDTO) userUpdateDTO);
        } 
        else if (existingUser instanceof Requester && userUpdateDTO instanceof RequesterUpdateDTO) {
            return requesterService.updateRequesterDetails((Requester) existingUser, (RequesterUpdateDTO) userUpdateDTO);
        }
        else {
            // Just update common fields for base User type
            return updateCommonFields(existingUser, userUpdateDTO);
        }
    }



    /**
     * Deletes a user's account
     * @param userId ID of the user requesting deletion
     * @param token Authentication token
     * @return void
     */
    public void deleteUser(Long userId, String token) {
        // First authenticate the requesting user
        User user = authenticateRequest(userId, token);
        
        // User can only delete their own account
        if (!user.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own account");
        }
        
        // Delete the user from the repository
        userRepository.delete(user);
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

