package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByUsername_success() {
    // given
    User user = new User();
    user.setEmail("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setPassword("password");
    user.setFirstName("Firstname");
    user.setLastName("Lastname");
    user.setPhoneNumber("123456789");
    user.setUserAccountType(UserAccountType.REQUESTER);

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByUsername(user.getUsername());

    // then
    assertNotNull(found.getUserId());
    assertEquals(found.getEmail(), user.getEmail());
    assertEquals(found.getUsername(), user.getUsername());
    assertEquals(found.getPassword(), user.getPassword());
    assertEquals(found.getFirstName(), user.getFirstName());
    assertEquals(found.getLastName(), user.getLastName());
    assertEquals(found.getPhoneNumber(), user.getPhoneNumber());
    assertEquals(found.getUserBio(), user.getUserBio());
    assertEquals(found.getProfilePicturePath(), user.getProfilePicturePath());
    assertEquals(found.getBirthDate(), user.getBirthDate());
    assertEquals(found.getUserAccountType(), user.getUserAccountType());
  }
}
