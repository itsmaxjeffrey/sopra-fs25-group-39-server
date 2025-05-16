package ch.uzh.ifi.hase.soprafs24.security.authorization.service;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

@Service
public class AuthorizationService {
    private final UserRepository userRepository;


    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Authenticates a user by validating their token
     * @return The authenticated User object if successful, null if authentication fails
     */
    public User authenticateUser(Long userId, String token) {
        // log.info("Authenticating user with userId={} and token={}", userId, token);

        if (token == null || token.isEmpty() || userId == null) {
            return null;
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !token.equals(user.getToken())) {
            return null;
        }
        
        return user;
    }
    
    /**
     * Authorizes a user for a specific account type
     * @return true if user is authorized for the specified account type
     */
    public boolean authorizeUser(Long userId, String token, UserAccountType requiredAccountType) {
        User user = authenticateUser(userId, token);
        return user != null && user.getUserAccountType() == requiredAccountType;
    }
}