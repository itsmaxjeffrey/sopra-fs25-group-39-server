package ch.uzh.ifi.hase.soprafs24.user.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.Application;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.DTO.request.update.BaseUserUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.DTO.response.PublicUserDTO;
import ch.uzh.ifi.hase.soprafs24.user.mapper.PublicUserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.user.model.User;
import ch.uzh.ifi.hase.soprafs24.user.service.UserService;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final Application application;

    private final AuthorizationService authorizationService;
    private final PublicUserDTOMapper publicUserDTOMapper;
    private final UserService userService;

    public UserController(
        AuthorizationService authorizationService,
        PublicUserDTOMapper publicUserDTOMapper,
        UserService userService
    , Application application){
        this.authorizationService = authorizationService;
        this.publicUserDTOMapper = publicUserDTOMapper;
        this.userService = userService;
        this.application = application;

    }



    //get single user by id. a logged in user can see their own profile. from others they can only see public info(publicUserDTO) 
    @GetMapping("/{paramUserId}")
    public ResponseEntity<?> getUserById(
        @RequestHeader("UserId") Long userId, 
        @RequestHeader("Authorization") String token, 
        @PathVariable("paramUserId") Long paramUserId) {
           try{ 

                //if a user is not logged in, they cannot see anything    
                User requestedUser = userService.getUser(userId, token);

                // check if the user is viewing their own profile
                if (requestedUser.getUserId().equals(paramUserId)) {

                    AuthenticatedUserDTO fullUserDTO = UserDTOMapper.INSTANCE.convertToDTO(requestedUser);
                    return ResponseEntity.ok(fullUserDTO);
                } else {
                    PublicUserDTO publicUserDTO = publicUserDTOMapper.convertToPublicUserDTO(requestedUser);
                    return ResponseEntity.ok(publicUserDTO);
                }
            }catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
            }

    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
        @PathVariable Long userId,
        @RequestHeader("Authorization") String token,
        @RequestBody BaseUserUpdateDTO userUpdateDTO) {
            
            try {
                User updatedUser = userService.editUser(userId, token, userUpdateDTO);
                AuthenticatedUserDTO userDTO = UserDTOMapper.INSTANCE.convertToDTO(updatedUser);
                return ResponseEntity.ok(userDTO);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
            }
}
            

    
    
    
}
