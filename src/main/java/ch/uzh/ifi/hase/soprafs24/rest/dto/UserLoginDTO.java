package ch.uzh.ifi.hase.soprafs24.rest.dto;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class UserLoginDTO {

    private String username;
    private String password;
    private UserAccountType userAccountType; // "DRIVER" or "REQUESTER"

}