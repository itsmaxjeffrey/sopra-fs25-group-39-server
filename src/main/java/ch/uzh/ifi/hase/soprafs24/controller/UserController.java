package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }


  @PostMapping("/api/v1/auth/register")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO registerUser(@RequestBody UserPostDTO userPostDTO) {
    if (userPostDTO.getUsername() == null || userPostDTO.getPassword() == null || userPostDTO.getUserAccountType() == null || userPostDTO.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, password , email or account type is null");
    }

    User userInput;
    User createdUser;

    if (userPostDTO.getUserAccountType() == UserAccountType.DRIVER) {
      userInput = DTOMapper.INSTANCE.convertUserPostDTOtoDriverEntity(userPostDTO);
      createdUser = userService.createDriver(userInput);
      return DTOMapper.INSTANCE.convertDriverEntityToUserGetDTO((Driver)createdUser);
      
    } 
    
    else if (userPostDTO.getUserAccountType() == UserAccountType.REQUESTER) {
      userInput = DTOMapper.INSTANCE.convertUserPostDTOtoRequesterEntity(userPostDTO);
      createdUser = userService.createRequester(userInput);
      return DTOMapper.INSTANCE.convertRequesterEntityToUserGetDTO((Requester)createdUser);
    } 
    
    else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account type");
    }
    
  }

}
