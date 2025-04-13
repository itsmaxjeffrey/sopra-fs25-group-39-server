package ch.uzh.ifi.hase.soprafs24.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.user.model.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);
  Optional<User> findByUsername(String username);
  Optional<User> findByToken(String token);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
  boolean existsByPhoneNumber(String phoneNumber);
  boolean existsByToken(String token);
  // JPA dynamically implements "SELECT * FROM user WHERE phoneNumber = :phoneNumber"
  User findByPhoneNumber(String phoneNumber);
  Optional<User> findByUserId(Long userId);
  boolean existsByUsernameAndUserIdNot(String username, Long userId);
  boolean existsByEmailAndUserIdNot(String email, Long userId);
  boolean existsByPhoneNumberAndUserIdNot(String phoneNumber, Long userId);

}
