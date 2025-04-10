package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;

public class RequesterRegisterDTO extends BaseUserRegisterDTO {

    // Add this constructor
    public RequesterRegisterDTO() {
        this.setUserAccountType(UserAccountType.REQUESTER);
    }
    
}
