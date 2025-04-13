package ch.uzh.ifi.hase.soprafs24.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.exceptions.UserNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.BaseUserUpdateDTO;

/**
 * Abstract base class for user services
 * Contains common functionality for authentication and user operations
 */
public abstract class AbstractUserService {
    protected final UserRepository userRepository;
    protected final AuthorizationService authorizationService;

    protected AbstractUserService(UserRepository userRepository, 
                              AuthorizationService authorizationService) {
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
    }

    /**
     * Authenticates a user request
     */
    protected User authenticateRequest(Long userId, String token) {
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
        }
        return authenticatedUser;
    }

    /**
     * Finds a user by ID
     */
    protected User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Updates common user fields
     */
    protected User updateCommonFields(User existingUser, BaseUserUpdateDTO updates) {
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
        
        if (updates.getBirthDate() != null) {
            existingUser.setBirthDate(updates.getBirthDate());
        }
        
        if (updates.getProfilePicturePath() != null) {
            existingUser.setProfilePicturePath(updates.getProfilePicturePath());
        }
        
        return existingUser;
    }

    /**
     * Saves a user and flushes the repository
     */
    protected User saveUser(User user) {
        User savedUser = userRepository.save(user);
        userRepository.flush();
        return savedUser;
    }
}