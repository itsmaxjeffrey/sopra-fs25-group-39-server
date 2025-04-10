package ch.uzh.ifi.hase.soprafs24.user;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserUpdateDTO {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String userBio;
    private LocalDate birthDate;
    private String profilePicturePath;
    // No sensitive fields like password should be here
}