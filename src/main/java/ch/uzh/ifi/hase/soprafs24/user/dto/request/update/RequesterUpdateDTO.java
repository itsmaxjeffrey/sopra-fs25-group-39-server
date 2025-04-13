package ch.uzh.ifi.hase.soprafs24.user.dto.request.update;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RequesterUpdateDTO extends BaseUserUpdateDTO {
    // Add any requester-specific fields here if needed
    
    public RequesterUpdateDTO() {
        this.setUserAccountType(UserAccountType.REQUESTER);
    }
}