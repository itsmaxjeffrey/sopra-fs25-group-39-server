package ch.uzh.ifi.hase.soprafs24.user;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PublicUserDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String userBio;
    private String profilePicturePath;
    private UserAccountType userAccountType;
}
