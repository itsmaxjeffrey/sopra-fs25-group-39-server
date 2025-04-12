package ch.uzh.ifi.hase.soprafs24.security.authentication.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request.BaseUserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.security.registration.service.TokenService;
import ch.uzh.ifi.hase.soprafs24.user.model.User;

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
        
        // Check password (in a real app, use proper password encryption)
        if (!user.getPassword().equals(baseUserLoginDTO.getPassword())) {
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


}