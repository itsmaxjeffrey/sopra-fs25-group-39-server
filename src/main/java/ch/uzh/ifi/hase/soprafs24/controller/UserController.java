package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.CarService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
  private final CarService carService;
  private final LocationService locationService;

  UserController(UserService userService, CarService carService, LocationService locationService) {
    this.userService = userService;
    this.carService = carService;
    this.locationService = locationService;
  }

  /**
   * Basic registration endpoint (for backward compatibility)
   */
  @PostMapping("/api/v1/auth/register")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO registerUser(@RequestBody UserPostDTO userPostDTO) {
    validateUserPostDTO(userPostDTO);

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

  /**
   * Register requester with file upload support
   */
  @PostMapping("/api/v1/auth/register/requester")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO registerRequesterWithFiles(
      @RequestPart("userData") UserPostDTO userPostDTO,
      @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

    validateUserPostDTO(userPostDTO);
    
    if (userPostDTO.getUserAccountType() != UserAccountType.REQUESTER) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User account type must be REQUESTER");
    }
    
    // Convert DTO to entity
    Requester requesterData = DTOMapper.INSTANCE.convertUserPostDTOtoRequesterEntity(userPostDTO);
    
    // Parse birth date if provided
    if (userPostDTO.getBirthDate() != null && !userPostDTO.getBirthDate().isBlank()) {
      LocalDate birthDate = userService.parseBirthDate(userPostDTO.getBirthDate());
      requesterData.setBirthDate(birthDate);
    }
    
    // Create requester with file uploads
    Requester createdRequester = userService.createRequesterWithFiles(requesterData, profilePicture);
    
    return DTOMapper.INSTANCE.convertRequesterEntityToUserGetDTO(createdRequester);
  }

  /**
   * Register driver with file upload support
   */
  @PostMapping("/api/v1/auth/register/driver")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO registerDriverWithFiles(
      @RequestPart("userData") UserPostDTO userPostDTO,
      @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
      @RequestPart("driverLicense") MultipartFile driverLicense,
      @RequestPart(value = "driverInsurance", required = false) MultipartFile driverInsurance,
      @RequestPart(value = "carPicture", required = false) MultipartFile carPicture,
      @RequestPart("carData") CarDTO carDTO,
      @RequestPart("locationData") LocationDTO locationDTO) {

    validateUserPostDTO(userPostDTO);
    
    if (userPostDTO.getUserAccountType() != UserAccountType.DRIVER) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User account type must be DRIVER");
    }
    
    // Convert DTOs to entities
    Driver driverData = DTOMapper.INSTANCE.convertUserPostDTOtoDriverEntity(userPostDTO);
    Car car = DTOMapper.INSTANCE.convertCarDTOtoEntity(carDTO);
    Location location = DTOMapper.INSTANCE.convertLocationDTOtoEntity(locationDTO);
    
    // Parse birth date if provided
    if (userPostDTO.getBirthDate() != null && !userPostDTO.getBirthDate().isBlank()) {
      LocalDate birthDate = userService.parseBirthDate(userPostDTO.getBirthDate());
      driverData.setBirthDate(birthDate);
    }
    
    // Create car and location first
    car = carService.createCar(car);
    location = locationService.createLocation(location);
    
    // Create driver with all related entities and file uploads
    Driver createdDriver = userService.createDriverWithFiles(
        driverData, profilePicture, driverLicense, driverInsurance, carPicture, car, location);
    
    return DTOMapper.INSTANCE.convertDriverEntityToUserGetDTO(createdDriver);
  }

  /**
   * Get all users
   */
  @GetMapping("/api/v1/users")
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
   * Validate the required fields in user post DTO
   */
  private void validateUserPostDTO(UserPostDTO userPostDTO) {
    if (userPostDTO.getUsername() == null || userPostDTO.getUsername().trim().isBlank() ||
        userPostDTO.getPassword() == null || userPostDTO.getPassword().trim().isBlank() ||
        userPostDTO.getUserAccountType() == null ||
        userPostDTO.getEmail() == null || userPostDTO.getEmail().trim().isBlank() ||
        userPostDTO.getFirstName() == null || userPostDTO.getFirstName().trim().isBlank() ||
        userPostDTO.getLastName() == null || userPostDTO.getLastName().trim().isBlank() ||
        userPostDTO.getPhoneNumber() == null || userPostDTO.getPhoneNumber().trim().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
          "Required fields are missing or blank. Please provide non-blank username, password, email, first name, last name, phone number, and account type.");
    }
  }
}
