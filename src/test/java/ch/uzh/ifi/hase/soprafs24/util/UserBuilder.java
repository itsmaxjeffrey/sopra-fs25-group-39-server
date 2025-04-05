package ch.uzh.ifi.hase.soprafs24.util;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.User;

/*
 * 
 */

public class UserBuilder {
    private final User user;

    public UserBuilder() {
        user = new User();
        user.setFirstName("Max");
        user.setLastName("Muster");
        user.setUsername("MaxMuster03");
        user.setPassword("Password123");
        user.setEmail("maxmuster@uzh.ch");
        user.setUserAccountType(UserAccountType.REQUESTER);
        user.setBirthDate(LocalDate.of(2003, 10, 23));
        user.setProfilePicturePath("/images/profile/default.png");
        user.setWalletBalance(100.0);
        user.setPhoneNumber("+41774882183");
        user.setUserBio("Default bio");
        user.setToken("96a69961-9db6-42e7-b31b-33fd9ddf2a9a");
    }

    public UserBuilder withAccountType(UserAccountType accountType) {
        user.setUserAccountType(accountType);
        return this;
    }

    public UserBuilder withUsername(String username) {
        user.setUsername(username);
        return this;
    }

    public UserBuilder withEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder withPhoneNumber(String phoneNumber) {
        user.setPhoneNumber(phoneNumber);
        return this;
    }

    public User build() {
        return user;
    }
}
