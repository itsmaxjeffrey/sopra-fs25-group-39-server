package ch.uzh.ifi.hase.soprafs24.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final FileStorageService fileStorageService;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository, FileStorageService fileStorageService) {
    this.userRepository = userRepository;
    this.fileStorageService = fileStorageService;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createRequester(User newUser) {
    checkUserCredentialUniquenes(newUser);
    newUser.setUserToken(UUID.randomUUID().toString());
    // saves the given entity but data is only persisted in the database once flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User createDriver(User newUser) {
    checkUserCredentialUniquenes(newUser);
    newUser.setUserToken(UUID.randomUUID().toString());
    // saves the given entity but data is only persisted in the database once flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * Creates a new requester with all required fields and file uploads
   * 
   * @param userDto Data transfer object with user information
   * @param profilePicture Optional profile picture file
   * @return The created requester
   */
  public Requester createRequesterWithFiles(User requesterData, MultipartFile profilePicture) {
    if (!(requesterData instanceof Requester)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user type provided");
    }
    
    Requester requester = (Requester) requesterData;
    
    // Handle profile picture upload if provided
    if (profilePicture != null && !profilePicture.isEmpty()) {
        String profilePicturePath = fileStorageService.storeFile(profilePicture, "profile-pictures");
        requester.setProfilePicturePath(profilePicturePath);
    }
    
    // Save requester to database
    checkUserCredentialUniqueness(requester);
    requester = (Requester) userRepository.save(requester);
    userRepository.flush();
    
    log.debug("Created Information for Requester: {}", requester);
    return requester;
  }

  /**
   * Creates a new driver with all required fields and file uploads
   * 
   * @param driverData Driver entity with basic information
   * @param profilePicture Optional profile picture file
   * @param driverLicense Driver license document (required)
   * @param driverInsurance Optional driver insurance document
   * @param carPicture Optional car picture
   * @param car Car entity with details
   * @param location Location entity with driver's location
   * @return The created driver
   */
  public Driver createDriverWithFiles(User driverData, MultipartFile profilePicture, 
                                     MultipartFile driverLicense, MultipartFile driverInsurance,
                                     MultipartFile carPicture, Car car, Location location) {
    if (!(driverData instanceof Driver)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user type provided");
    }
    
    Driver driver = (Driver) driverData;
    
    // Validate required files
    if (driverLicense == null || driverLicense.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver license document is required");
    }
    
    // Handle file uploads
    if (profilePicture != null && !profilePicture.isEmpty()) {
        String profilePicturePath = fileStorageService.storeFile(profilePicture, "profile-pictures");
        driver.setProfilePicturePath(profilePicturePath);
    }
    
    // Handle driver license (required)
    String driverLicensePath = fileStorageService.storeFile(driverLicense, "driver-licenses");
    driver.setDriverLicensePath(driverLicensePath);
    
    // Handle driver insurance if provided
    if (driverInsurance != null && !driverInsurance.isEmpty()) {
        String driverInsurancePath = fileStorageService.storeFile(driverInsurance, "driver-insurances");
        driver.setDriverInsurancePath(driverInsurancePath);
    }
    
    // Handle car picture if provided
    if (car != null && carPicture != null && !carPicture.isEmpty()) {
        String carPicturePath = fileStorageService.storeFile(carPicture, "car-pictures");
        car.setCarPicturePath(carPicturePath);
    }
    
    // Set car and location
    if (car != null) {
        driver.setCar(car);
    }
    
    if (location != null) {
        driver.setLocation(location);
    }
    
    // Save driver to database
    checkUserCredentialUniqueness(driver);
    driver = (Driver) userRepository.save(driver);
    userRepository.flush();
    
    log.debug("Created Information for Driver: {}", driver);
    return driver;
  }

  /**
   * Parse birthdate from string to LocalDate
   * 
   * @param birthDateStr Birth date in string format (yyyy-MM-dd)
   * @return LocalDate object
   */
  public LocalDate parseBirthDate(String birthDateStr) {
    if (birthDateStr == null || birthDateStr.isEmpty()) {
        return null;
    }
    
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(birthDateStr, formatter);
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid birth date format. Use yyyy-MM-dd");
    }
  }

  /**
   * Check if given username, email, phone number are unique. Throw personalized error message otherwise. 
   * 
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkUserCredentialUniqueness(User userToBeCreated) {
    List<String> notUniqueAttributes = new ArrayList<>();

    if (userRepository.findByUsername(userToBeCreated.getUsername()) != null) { notUniqueAttributes.add("Username"); }
    if (userRepository.findByEmail(userToBeCreated.getEmail()) != null) { notUniqueAttributes.add("Mail Adress"); }
    if (userRepository.findByPhoneNumber(userToBeCreated.getPhoneNumber()) != null) { notUniqueAttributes.add("Phone Number"); }

    if (!notUniqueAttributes.isEmpty()) {
      String errorMessage = String.format(
          "The %s provided %s not unique. Therefore, the account could not be created!",
          String.join(", ", notUniqueAttributes),
          notUniqueAttributes.size() > 1 ? "are" : "is"
        );
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }
  }
}
