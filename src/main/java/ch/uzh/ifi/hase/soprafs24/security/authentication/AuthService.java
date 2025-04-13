package ch.uzh.ifi.hase.soprafs24.security.authentication;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.login.BaseUserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.security.TokenService;

//this file handles login and logout
@Service
@Transactional
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthService(
        UserRepository userRepository,
        TokenService tokenService){
            this.userRepository = userRepository;
            this.tokenService = tokenService;
    }


   
    
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
    public void logoutUser(String token) {
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required");
        }
        
        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        User user = userOptional.get();
        
        // Invalidate token
        user.setToken(null);
        userRepository.save(user);
        userRepository.flush();

        
        log.debug("User logged out: {}", user.getUsername());
    }


}