package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthenticatedUserDTO {
    // Authentication details
    private String token;
    private Long userId;
    private UserAccountType userAccountType;
    
    // Common user fields
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Double walletBalance;
    private LocalDate birthDate;
    private String userBio;
    private String profilePicturePath;
}