// package ch.uzh.ifi.hase.soprafs24.service;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.HttpStatus;
// import org.springframework.test.context.web.WebAppConfiguration;
// import org.springframework.web.server.ResponseStatusException;

// import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
// import ch.uzh.ifi.hase.soprafs24.entity.User;
// import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
// import ch.uzh.ifi.hase.soprafs24.util.UserBuilder;

// /**
//  * Test class for the UserResource REST resource.
//  * Use the UserBuilder to build test Users. 
//  * 
//  * @see UserBuilder
//  * @see UserService
//  */
// @WebAppConfiguration
// @SpringBootTest
// public class UserServiceIntegrationTest {

//   @Qualifier("userRepository")
//   @Autowired
//   private UserRepository userRepository;

//   @Autowired
//   private UserService userService;

//   @BeforeEach
//   public void setup() {
//     userRepository.deleteAll();
//   }

//   @Test
//   public void createUser_validInputs_success() {
//     // given
//     assertNull(userRepository.findByUsername("testUsername"));

//     User testUser = new UserBuilder().withAccountType(UserAccountType.REQUESTER).build();

//     // when
//     User createdUser = userService.createRequester(testUser);

//     // then
//     assertEquals(testUser.getPhoneNumber(), createdUser.getPhoneNumber());
//     assertEquals(testUser.getEmail(), createdUser.getEmail());

//     assertEquals(testUser.getUserId(), createdUser.getUserId());
//     assertEquals(testUser.getUsername(), createdUser.getUsername());
//     assertEquals(testUser.getFirstName(), createdUser.getFirstName());
//     assertEquals(testUser.getLastName(), createdUser.getLastName());

//     assertEquals(testUser.getUserBio(), createdUser.getUserBio());
//     // assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
//   }

//   @Test
//   public void createUser_not_unique_userName_failure() {
//     // given
//     assertNull(userRepository.findByUsername("testUsername"));

//     User existingUser = new UserBuilder().withUsername("testUsername").withEmail("test1@example.com").withPhoneNumber("+123456789").build();
//     userService.createRequester(existingUser);

//     User duplicateUser = new UserBuilder().withUsername("testUsername").withEmail("test2@example.com").withPhoneNumber("+987654321").build();

//     //when
//     ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.createRequester(duplicateUser));

//     //then 
//     assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//     assertEquals(exception.getReason(), "The Username provided is not unique. Therefore, the account could not be created!");
//   }  
  
//   @Test
//   public void createUser_not_unique_MailAdress_failure() {
//     // given
//     assertNull(userRepository.findByUsername("testUsername"));

//     User existingUser = new UserBuilder().withUsername("testUsername1").withPhoneNumber("+123456789").build();
//     userService.createRequester(existingUser);

//     User duplicateUser = new UserBuilder().withUsername("testUsername2").withPhoneNumber("+987654321").build();

//     //when
//     ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.createRequester(duplicateUser));

//     //then 
//     assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//     assertEquals(exception.getReason(), "The Mail Adress provided is not unique. Therefore, the account could not be created!");
//   }

//   @Test
//   public void createUser_not_unique_PhoneNumber_failure() {
//     // given
//     assertNull(userRepository.findByUsername("testUsername"));

//     User existingUser = new UserBuilder().withUsername("testUsername1").withEmail("test1@example.com").build();
//     userService.createRequester(existingUser);

//     User duplicateUser = new UserBuilder().withUsername("testUsername2").withEmail("test2@example.com").build();

//     //when
//     ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.createRequester(duplicateUser));

//     //then 
//     assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//     assertEquals(exception.getReason(), "The Phone Number provided is not unique. Therefore, the account could not be created!");
//   }

//   @Test
//   public void createUser_not_unique_Username_MailAdress_PhoneNumber_failure() {
//     // given
//     assertNull(userRepository.findByUsername("testUsername"));

//     User existingUser = new UserBuilder().build();
//     userService.createRequester(existingUser);

//     User duplicateUser = new UserBuilder().build();

//     //when
//     ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.createRequester(duplicateUser));

//     //then 
//     assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//     assertEquals(exception.getReason(), "The Username, Mail Adress, Phone Number provided are not unique. Therefore, the account could not be created!");
//   }
// }
