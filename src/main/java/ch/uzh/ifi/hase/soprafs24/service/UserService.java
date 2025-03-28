package ch.uzh.ifi.hase.soprafs24.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createRequester(User newUser) {
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User createDriver(User newUser) {
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * Check if given username, email, phone number are unique. Throw personalized error message otherwise. 
   * 
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
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
