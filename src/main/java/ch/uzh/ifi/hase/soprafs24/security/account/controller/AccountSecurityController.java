package ch.uzh.ifi.hase.soprafs24.security.account.controller; // Updated package

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.service.UserService;
// Updated import path for the DTO
import ch.uzh.ifi.hase.soprafs24.security.account.dto.request.UserDeleteRequestDTO;

@RestController
// Updated base path to reflect the new structure, e.g., /api/v1/security/account
@RequestMapping("/api/v1/auth")
public class AccountSecurityController {

    private final UserService userService;
    private final AuthorizationService authorizationService;

    public AccountSecurityController(UserService userService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    /**
     * Endpoint to initiate account deletion with email verification.
     * POST /api/v1/auth/users/{userId}
     */
    // Changed path to be relative to the new base path
    @PostMapping("/users/{userId}")
    public ResponseEntity<Object> deleteAccountVerified(
            @RequestHeader("UserId") Long userId,
            @RequestHeader("Authorization") String token,
            @RequestBody UserDeleteRequestDTO deleteRequest) {

        try {
            User authenticatedUser = authorizationService.authenticateUser(userId, token);
            if (authenticatedUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            if (deleteRequest == null || deleteRequest.getEmail() == null || deleteRequest.getEmail().isEmpty()) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required for verification");
            }

            if (!authenticatedUser.getEmail().equalsIgnoreCase(deleteRequest.getEmail())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Entered email does not match account email");
            }

            userService.deleteUser(userId, token);

            return ResponseEntity.noContent().build();

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getRawStatusCode())
                    .body(Map.of("message", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred during account deletion."));
        }
    }

}
