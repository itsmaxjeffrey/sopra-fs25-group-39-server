// package ch.uzh.ifi.hase.soprafs24.repository;

// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

// import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
// import ch.uzh.ifi.hase.soprafs24.entity.User;

// @DataJpaTest
// class UserRepositoryIntegrationTest {

//   @Autowired
//   private TestEntityManager entityManager;

//   @Autowired
//   private UserRepository userRepository;

//   @Test
//   public void findByUsername_success() {
//     // given
//     User user = new UserBuilder().build();

//     entityManager.persist(user);
//     entityManager.flush();
//     // when
//     Optional<User> found = userRepository.findByUsername(user.getUsername());

//     // then
//     assertTrue(found.isPresent());
//     User foundUser = found.get();
//     assertNotNull(foundUser.getUserId());
//     assertEquals(user.getEmail(), foundUser.getEmail());
//     assertEquals(user.getUsername(), foundUser.getUsername());
//     assertEquals(user.getPassword(), foundUser.getPassword());
//     assertEquals(user.getFirstName(), foundUser.getFirstName());
//     assertEquals(user.getLastName(), foundUser.getLastName());
//     assertEquals(user.getPhoneNumber(), foundUser.getPhoneNumber());
//     assertEquals(user.getUserBio(), foundUser.getUserBio());
//     assertEquals(user.getProfilePicturePath(), foundUser.getProfilePicturePath());
//     assertEquals(user.getBirthDate(), foundUser.getBirthDate());
//     assertEquals(user.getUserAccountType(), foundUser.getUserAccountType());

//   }
// }
