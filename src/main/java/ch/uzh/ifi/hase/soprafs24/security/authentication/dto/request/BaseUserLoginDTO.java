package ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BaseUserLoginDTO {
    private String username;
    private String password;
}
