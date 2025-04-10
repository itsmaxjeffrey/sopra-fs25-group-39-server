package ch.uzh.ifi.hase.soprafs24.user;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.exceptions.UserNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.authorization.AuthorizationService;

@Service
public class UserService {

    private final AuthorizationService authorizationService;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, AuthorizationService authorizationService) {
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
    }
    
    public User getUserById(Long userId, String token) {
        // First authenticate the requesting user
        User authenticatedUser = authorizationService.authenticateUser(userId, token);
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
        }
        
        
        // Then get the requested user (could be different from the authenticated user)
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
    }


}
