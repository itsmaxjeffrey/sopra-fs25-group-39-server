package ch.uzh.ifi.hase.soprafs24.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.BaseUserRegisterDTO;

@Service
public class TokenService {
    private final UserRepository userRepository;

    public TokenService(
        UserRepository userRepository){
            this.userRepository = userRepository;
        }

    
    //retrurms true if the token is valid    
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        return userRepository.existsByToken(token);
    }
    
    //check if the token of the user matches the id it has.
    public boolean validateTokenForUser(Long userId, String token) {
        
        //token empty case
        if (token == null || token.isEmpty() || userId == null) {
            return false;
        }
        
        //id empty case
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        return token.equals(user.getToken());
    }


    //generate token
    public String generateToken() {
        return UUID.randomUUID().toString();
    }


}
