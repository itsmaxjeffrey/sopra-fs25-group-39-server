package ch.uzh.ifi.hase.soprafs24.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public UserNotFoundException(Long userId) {
        super(String.format("User with ID %d was not found", userId));
    }
    
    public UserNotFoundException(String message) {
        super(message);
    }
}