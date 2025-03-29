package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);
  User findByEmail(String email);

  // JPA dynamically implements "SELECT * FROM user WHERE phoneNumber = :phoneNumber"
  User findByPhoneNumber(String phoneNumber);
}
