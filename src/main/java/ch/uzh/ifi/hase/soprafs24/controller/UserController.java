package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user management (NOT authentication).
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  UserController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  /**
   * Get all users
   */
  @GetMapping("")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // Fetch all users from the service
    List<User> users = userService.getUsers();
    
    // Convert each user to a DTO based on their type
    return users.stream()
        .map(user -> {
          if (user instanceof Driver) {
            return DTOMapper.INSTANCE.convertDriverEntityToUserGetDTO((Driver) user);
          } else {
            return DTOMapper.INSTANCE.convertRequesterEntityToUserGetDTO(user);
          }
        })
        .toList();
  }

  /**
   * Get user by ID
   */
  @GetMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUserById(@PathVariable Long userId) {
    User user = userService.getUserById(userId);
    
    if (user instanceof Driver) {
      return DTOMapper.INSTANCE.convertDriverEntityToUserGetDTO((Driver) user);
    } else {
      return DTOMapper.INSTANCE.convertRequesterEntityToUserGetDTO(user);
    }
  }
  
  /**
   * Get requester-specific profile
   */
  @GetMapping("/requesters/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getRequesterById(@PathVariable Long userId) {
    Requester requester = userService.getRequesterById(userId);
    return DTOMapper.INSTANCE.convertRequesterEntityToUserGetDTO(requester);
  }
  
  /**
   * Get driver-specific profile
   */
  @GetMapping("/drivers/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getDriverById(@PathVariable Long userId) {
    Driver driver = userService.getDriverById(userId);
    return DTOMapper.INSTANCE.convertDriverEntityToUserGetDTO(driver);
  }

  /**
   * Get current user profile (from token)
   */
  @GetMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getCurrentUser(@RequestHeader("Authorization") String token) {
    User user = authService.getUserByToken(token);
    
    if (user instanceof Driver) {
      return DTOMapper.INSTANCE.convertDriverEntityToUserGetDTO((Driver) user);
    } else {
      return DTOMapper.INSTANCE.convertRequesterEntityToUserGetDTO(user);
    }
  }

  // /**
  //  * Update user profile
  //  */
  // @PutMapping("/{userId}")
  // @ResponseStatus(HttpStatus.OK)
  // @ResponseBody
  // public UserGetDTO updateUser(
  //     @PathVariable Long userId,
  //     @RequestHeader("Authorization") String token,
  //     @RequestBody UserUpdateDTO userUpdateDTO) {
    
  //   // Verify token belongs to this user
  //   if (!authService.validateTokenForUser(userId, token)) {
  //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to update this user");
  //   }
    
  //   User updatedUser = userService.updateUser(userId, userUpdateDTO);
    
  //   if (updatedUser instanceof Driver) {
  //     return DTOMapper.INSTANCE.convertDriverEntityToUserGetDTO((Driver) updatedUser);
  //   } else {
  //     return DTOMapper.INSTANCE.convertRequesterEntityToUserGetDTO(updatedUser);
  //   }
  // }

  // /**
  //  * Update user profile picture
  //  */
  // @PutMapping("/{userId}/profile-picture")
  // @ResponseStatus(HttpStatus.OK)
  // @ResponseBody
  // public Map<String, String> updateProfilePicture(
  //     @PathVariable Long userId,
  //     @RequestHeader("Authorization") String token,
  //     @RequestParam("file") MultipartFile profilePicture) {
    
  //   // Verify token belongs to this user
  //   if (!authService.validateTokenForUser(userId, token)) {
  //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to update this user");
  //   }
    
  //   String profilePicturePath = userService.updateProfilePicture(userId, profilePicture);
    
  //   Map<String, String> response = new HashMap<>();
  //   response.put("profilePicturePath", profilePicturePath);
  //   return response;
  // }

  // /**
  //  * Delete user account
  //  */
  // @DeleteMapping("/{userId}")
  // @ResponseStatus(HttpStatus.OK)
  // public Map<String, String> deleteUser(
  //     @PathVariable Long userId,
  //     @RequestHeader("Authorization") String token) {
    
  //   // Verify token belongs to this user
  //   if (!authService.validateTokenForUser(userId, token)) {
  //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this user");
  //   }
    
  //   userService.deleteUser(userId);
    
  //   Map<String, String> response = new HashMap<>();
  //   response.put("message", "User successfully deleted");
  //   return response;
  // }
}