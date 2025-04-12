package ch.uzh.ifi.hase.soprafs24.security.registration.dto;

import ch.uzh.ifi.hase.soprafs24.common.constant.UserAccountType;

public class RequesterRegisterDTO extends BaseUserRegisterDTO {

    // Add this constructor
    public RequesterRegisterDTO() {
        this.setUserAccountType(UserAccountType.REQUESTER);
    }
    
}
