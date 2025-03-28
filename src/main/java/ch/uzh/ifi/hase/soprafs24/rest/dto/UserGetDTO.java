package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;

public class UserGetDTO {

  private Long userId;
  private String firstName;
  private String lastName;
  private String username;
  private UserAccountType userAccountType;

  public Long getUserId() {
    return userId;
  }
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  public UserAccountType getUserAccountType() {
    return userAccountType;
  }
  public void setUserAccountType(UserAccountType userAccountType) {
    this.userAccountType = userAccountType;
  }
}
