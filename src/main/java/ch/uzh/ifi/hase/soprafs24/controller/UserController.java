package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.UserDTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;

    public UserController(UserRepository userRepository, UserDTOMapper userDTOMapper) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
    }

    /**
     * Get user details by ID
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthenticatedUserDTO> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        // Validate token (optional, depending on your security setup)
        if (!token.equals("Bearer " + user.getToken())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        AuthenticatedUserDTO authenticatedUserDTO = userDTOMapper.convertToDTO(user);
        return new ResponseEntity<>(authenticatedUserDTO, HttpStatus.OK);
    }
}