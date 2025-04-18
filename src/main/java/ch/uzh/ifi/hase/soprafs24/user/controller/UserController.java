package ch.uzh.ifi.hase.soprafs24.user.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.Application;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.BaseUserUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.response.PublicUserDTO;
import ch.uzh.ifi.hase.soprafs24.user.mapper.PublicUserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final PublicUserDTOMapper publicUserDTOMapper;
    private final UserService userService;

    public UserController(
        AuthorizationService authorizationService,
        PublicUserDTOMapper publicUserDTOMapper,
        UserService userService,
        Application application) {
        this.publicUserDTOMapper = publicUserDTOMapper;
        this.userService = userService;
    }

    @GetMapping("/{paramUserId}")
    public ResponseEntity<?> getUserById(
        @RequestHeader("UserId") Long userId, 
        @RequestHeader("Authorization") String token, 
        @PathVariable("paramUserId") Long paramUserId) {
        try {
            User targetUser = userService.getUserById(userId, token, paramUserId);

            if (userId.equals(paramUserId)) {
                AuthenticatedUserDTO fullUserDTO = UserDTOMapper.INSTANCE.convertToDTO(targetUser);
                return ResponseEntity.ok(fullUserDTO);
            } else {
                PublicUserDTO publicUserDTO = publicUserDTOMapper.convertToPublicUserDTO(targetUser);
                return ResponseEntity.ok(publicUserDTO);
            }
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getRawStatusCode())
                .body(Map.of("message", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
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
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getRawStatusCode())
                .body(Map.of("message", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
        @PathVariable Long userId,
        @RequestHeader("Authorization") String token) {
        try {
            userService.deleteUser(userId, token);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getRawStatusCode())
                .body(Map.of("message", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
        }
    }
}
