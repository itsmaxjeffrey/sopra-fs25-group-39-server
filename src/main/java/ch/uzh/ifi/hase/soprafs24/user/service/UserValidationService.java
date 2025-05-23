package ch.uzh.ifi.hase.soprafs24.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.BaseUserUpdateDTO;

/**
 * Service for validating user data
 * This class handles validation logic that was previously in UserService
 */
@Service
public class UserValidationService {

    private final UserRepository userRepository;
    
    public UserValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Validates that all unique fields in the update DTO don't conflict with existing users
     */
    public void validateUniqueFields(BaseUserUpdateDTO updates, User existingUser) {
        validateUniqueUsername(updates, existingUser);
        validateUniqueEmail(updates, existingUser);
        validateUniquePhoneNumber(updates, existingUser);
    }
    
    /**
     * Validates username uniqueness
     */
    private void validateUniqueUsername(BaseUserUpdateDTO updates, User existingUser) {
        if (updates.getUsername() != null && 
            !updates.getUsername().isEmpty() && 
            !updates.getUsername().equals(existingUser.getUsername()) &&
            userRepository.existsByUsernameAndUserIdNot(updates.getUsername(), existingUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }
    }
    
    /**
     * Validates email uniqueness
     */
    private void validateUniqueEmail(BaseUserUpdateDTO updates, User existingUser) {
        if (updates.getEmail() != null && 
            !updates.getEmail().isEmpty() && 
            !updates.getEmail().equals(existingUser.getEmail()) &&
            userRepository.existsByEmailAndUserIdNot(updates.getEmail(), existingUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        }
    }
    
    /**
     * Validates phone number uniqueness
     */
    private void validateUniquePhoneNumber(BaseUserUpdateDTO updates, User existingUser) {
        if (updates.getPhoneNumber() != null && 
            !updates.getPhoneNumber().isEmpty() && 
            !updates.getPhoneNumber().equals(existingUser.getPhoneNumber()) &&
            userRepository.existsByPhoneNumberAndUserIdNot(updates.getPhoneNumber(), existingUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number is already taken");
        }
    }
    
    /**
     * Validates that user account type hasn't changed
     */
    public void validateUserAccountType(User existingUser, BaseUserUpdateDTO userUpdateDTO) {
        if (!existingUser.getUserAccountType().equals(userUpdateDTO.getUserAccountType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot change user account type from " + existingUser.getUserAccountType() + 
                " to " + userUpdateDTO.getUserAccountType());
        }
    }
    
    /**
     * Verifies that a user is only editing their own profile
     */
    public void validateEditPermission(Long userId, BaseUserUpdateDTO userUpdateDTO) {
        if (!userId.equals(userUpdateDTO.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own profile");
        }
    }

    /**
     * Validates that the birthdate is not in the future
     */
    public void validateBirthDate(java.time.LocalDate birthDate) {
        if (birthDate != null && birthDate.isAfter(java.time.LocalDate.now())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Birthdate cannot be in the future");
        }
    }
}