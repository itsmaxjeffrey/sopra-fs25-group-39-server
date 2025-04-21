package ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PasswordChangeDTO {

    private String currentPassword;

    // Consider adding complexity validation here if needed (e.g., @Pattern)
    private String newPassword;
}
