package ch.uzh.ifi.hase.soprafs24.security.authentication.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request.BaseUserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request.PasswordChangeDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.security.registration.service.TokenService;

//this file handles login and logout
@Service
@Transactional
public class AuthService {
    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final AuthorizationService authorizationService;

    public AuthService(
        UserRepository userRepository,
        TokenService tokenService,
        AuthorizationService authorizationService){
            this.userRepository = userRepository;
            this.tokenService = tokenService;
            this.authorizationService = authorizationService;}
    


   
    
    //handle login
    public User loginUser(BaseUserLoginDTO baseUserLoginDTO) {
        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(baseUserLoginDTO.getUsername());
            
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "User not found");
        }
        
        User user = userOptional.get();

        // *** SECURITY WARNING: Passwords should be hashed using a strong algorithm (e.g., BCrypt) ***
        // *** See https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html ***
        
        // Trim password input for consistent comparison with changePassword logic
        String passwordInput = baseUserLoginDTO.getPassword() != null ? baseUserLoginDTO.getPassword().trim() : null;

        // Check password 
        if (passwordInput == null || !user.getPassword().equals(passwordInput)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                "Password is incorrect");
        }
        
        // Generate new token for this session
        String token = tokenService.generateToken();
        user.setToken(token);
        
        // Save updated user with new token
        user = userRepository.save(user);
        userRepository.flush();


        
        log.debug("User logged in: {}", user.getUsername());
        return user;
    }
    

    //handle logout
    public void logoutUser(Long userId, String token) {

        User user = authorizationService.authenticateUser(userId, token);
        if (user == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        user.setToken(null);
        userRepository.save(user);
        userRepository.flush();

        
        log.debug("User logged out: {}", user.getUsername());
    }

    //handle password change
    public void changePassword(Long userId, String token, PasswordChangeDTO passwordChangeDTO) {
        User user = authorizationService.authenticateUser(userId, token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token or user ID");
        }

        // Trim input passwords to remove potential leading/trailing whitespace
        String currentPasswordInput = passwordChangeDTO.getCurrentPassword() != null ? passwordChangeDTO.getCurrentPassword().trim() : null;
        String newPasswordInput = passwordChangeDTO.getNewPassword() != null ? passwordChangeDTO.getNewPassword().trim() : null;

        // Check if inputs are empty after trimming
        if (currentPasswordInput == null || currentPasswordInput.isEmpty()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password cannot be empty");
        }
        if (newPasswordInput == null || newPasswordInput.isEmpty()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password cannot be empty");
        }

        // Add detailed logging before comparison
        log.debug("Attempting password change for user: {}", user.getUsername());
        log.debug("Stored Password: '{}'", user.getPassword());
        log.debug("Provided Current Password (trimmed): '{}'", currentPasswordInput);

        // Verify trimmed current password
        if (!user.getPassword().equals(currentPasswordInput)) { // Compare with trimmed input
            log.warn("Password comparison failed for user: {}", user.getUsername()); // Log failure
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect current password");
        }

        // Set trimmed new password
        user.setPassword(newPasswordInput); // Save trimmed password
        userRepository.save(user);
        userRepository.flush();

        log.debug("Password changed successfully for user: {}", user.getUsername());
    }

}