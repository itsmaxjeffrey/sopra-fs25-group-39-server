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
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.Application;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.BaseUserUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.mapper.PublicUserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.user.service.UserService;
import ch.uzh.ifi.hase.soprafs24.user.dto.response.PublicUserDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.DriverUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.RequesterUpdateDTO;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final String MESSAGE_KEY = "message";

    private final PublicUserDTOMapper publicUserDTOMapper;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(
        PublicUserDTOMapper publicUserDTOMapper,
        UserService userService,
        Application application,
        ObjectMapper objectMapper) {
        this.publicUserDTOMapper = publicUserDTOMapper;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{paramUserId}")
    public ResponseEntity<Object> getUserById(
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
                    .body(Map.of(MESSAGE_KEY, e.getReason()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(MESSAGE_KEY, e.getMessage()));
            }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
        @PathVariable Long userId,
        @RequestHeader("Authorization") String token,
        @RequestBody Map<String, Object> payload) {
        try {
            String accountTypeStr = (String) payload.get("userAccountType");
            if (accountTypeStr == null) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userAccountType is missing in request body");
            }

            BaseUserUpdateDTO userUpdateDTO;
            UserAccountType accountType = UserAccountType.valueOf(accountTypeStr.toUpperCase());

            if (accountType == UserAccountType.DRIVER) {
                userUpdateDTO = objectMapper.convertValue(payload, DriverUpdateDTO.class);
            } else if (accountType == UserAccountType.REQUESTER) {
                userUpdateDTO = objectMapper.convertValue(payload, RequesterUpdateDTO.class);
            } else {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userAccountType");
            }

            User updatedUser = userService.editUser(userId, token, userUpdateDTO);
            AuthenticatedUserDTO userDTO = UserDTOMapper.INSTANCE.convertToDTO(updatedUser);
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userAccountType value provided", e);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getRawStatusCode())
                .body(Map.of(MESSAGE_KEY, e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }
}
