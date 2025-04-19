package ch.uzh.ifi.hase.soprafs24.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.util.UserBuilder;

@DataJpaTest
class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByUsername_success() {
    // given
    User user = new UserBuilder().build();

    entityManager.persist(user);
    entityManager.flush();
    // when
    Optional<User> found = userRepository.findByUsername(user.getUsername());

    // then
    assertTrue(found.isPresent());
    User foundUser = found.get();
    assertNotNull(foundUser.getUserId());
    assertEquals(user.getEmail(), foundUser.getEmail());
    assertEquals(user.getUsername(), foundUser.getUsername());
    assertEquals(user.getPassword(), foundUser.getPassword());
    assertEquals(user.getFirstName(), foundUser.getFirstName());
    assertEquals(user.getLastName(), foundUser.getLastName());
    assertEquals(user.getPhoneNumber(), foundUser.getPhoneNumber());
    assertEquals(user.getUserBio(), foundUser.getUserBio());
    assertEquals(user.getProfilePicturePath(), foundUser.getProfilePicturePath());
    assertEquals(user.getBirthDate(), foundUser.getBirthDate());
    assertEquals(user.getUserAccountType(), foundUser.getUserAccountType());
  }

  @Test
  void findByUsername_notFound() {
    // given
    String nonExistentUsername = "nonexistentuser";

    // when
    Optional<User> found = userRepository.findByUsername(nonExistentUsername);

    // then
    assertFalse(found.isPresent());
  }

  @Test
  void findByEmail_success() {
    // given
    User user = new UserBuilder().build();

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByEmail(user.getEmail());

    // then
    assertNotNull(found);
    assertEquals(user.getEmail(), found.getEmail());
  }

  @Test
  void findByEmail_notFound() {
    // given
    String nonExistentEmail = "nonexistent@example.com";

    // when
    User found = userRepository.findByEmail(nonExistentEmail);

    // then
    assertNull(found);
  }

  @Test
  void findByUserId_success() {
    // given
    User user = new UserBuilder().build();

    entityManager.persist(user);
    entityManager.flush();

    // when
    Optional<User> found = userRepository.findByUserId(user.getUserId());

    // then
    assertTrue(found.isPresent());
    assertEquals(user.getUserId(), found.get().getUserId());
  }

  @Test
  void findByUserId_notFound() {
    // given
    Long nonExistentId = 999999L;

    // when
    Optional<User> found = userRepository.findByUserId(nonExistentId);

    // then
    assertFalse(found.isPresent());
  }

  @Test
  void existsByUsername_success() {
    // given
    User user = new UserBuilder().build();

    entityManager.persist(user);
    entityManager.flush();

    // when
    boolean exists = userRepository.existsByUsername(user.getUsername());

    // then
    assertTrue(exists);
  }

  @Test
  void existsByUsername_notFound() {
    // given
    String nonExistentUsername = "nonexistentuser";

    // when
    boolean exists = userRepository.existsByUsername(nonExistentUsername);

    // then
    assertFalse(exists);
  }

  @Test
  void existsByEmail_success() {
    // given
    User user = new UserBuilder().build();

    entityManager.persist(user);
    entityManager.flush();

    // when
    boolean exists = userRepository.existsByEmail(user.getEmail());

    // then
    assertTrue(exists);
  }

  @Test
  void existsByEmail_notFound() {
    // given
    String nonExistentEmail = "nonexistent@example.com";

    // when
    boolean exists = userRepository.existsByEmail(nonExistentEmail);

    // then
    assertFalse(exists);
  }

  @Test
  void findByPhoneNumber_success() {
    // given
    User user = new UserBuilder().build();

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByPhoneNumber(user.getPhoneNumber());

    // then
    assertNotNull(found);
    assertEquals(user.getPhoneNumber(), found.getPhoneNumber());
  }

  @Test
  void findByPhoneNumber_notFound() {
    // given
    String nonExistentPhoneNumber = "+41791234567";

    // when
    User found = userRepository.findByPhoneNumber(nonExistentPhoneNumber);

    // then
    assertNull(found);
  }

  @Test
  void existsByPhoneNumber_success() {
    // given
    User user = new UserBuilder().build();

    entityManager.persist(user);
    entityManager.flush();

    // when
    boolean exists = userRepository.existsByPhoneNumber(user.getPhoneNumber());

    // then
    assertTrue(exists);
  }

  @Test
  void existsByPhoneNumber_notFound() {
    // given
    String nonExistentPhoneNumber = "+41791234567";

    // when
    boolean exists = userRepository.existsByPhoneNumber(nonExistentPhoneNumber);

    // then
    assertFalse(exists);
  }
}
