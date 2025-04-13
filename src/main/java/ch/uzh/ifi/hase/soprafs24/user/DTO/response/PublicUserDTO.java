package ch.uzh.ifi.hase.soprafs24.user.dto.response;

import ch.uzh.ifi.hase.soprafs24.common.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PublicUserDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePicturePath;
    private UserAccountType userAccountType;
}
