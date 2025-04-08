package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BaseUserRegisterDTO {
    // Common fields for all users
    private String username;
    private String password;
    private String email;
    private UserAccountType userAccountType;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String userBio;
    private LocalDate birthDate;
    private String profilePicturePath;
}