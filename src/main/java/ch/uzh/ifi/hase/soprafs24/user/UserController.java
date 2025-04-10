package ch.uzh.ifi.hase.soprafs24.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.authorization.AuthorizationService;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final AuthorizationService authorizationService;

    public UserController(AuthorizationService authorizationService){
        this.authorizationService = authorizationService;
    }



    //get single user by id
    @GetMapping("/{paramUserId}")
    public User getUserById(@RequestHeader("UserId") Long userId, @RequestHeader("Authorization") String token, @PathVariable("paramUserId") Long paramUserId) {
        User user = authorizationService.authenticateUser(paramUserId, token);
        if (user == null){
            return null;
        }
        return user;
    }
    
    
    
}
