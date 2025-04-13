package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register;

import java.time.LocalDate;

import javax.persistence.Column;

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

    @Column(nullable = true)
    private String userBio;

    private LocalDate birthDate;

    @Column(nullable = true)
    private String profilePicturePath;
}