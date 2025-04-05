package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.login;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BaseUserLoginDTO {
    private String username;
    private String password;
}
