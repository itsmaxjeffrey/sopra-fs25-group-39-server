package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Car;

public class UserPostDTO {

  private String firstName;
  private String lastName;
  private String username;
  private String password;
  private String email;
  private UserAccountType userAccountType;
  private String birthDate;
  private String profilePicturePath;
  private String phoneNumber;
  private String userBio;
  private Car car;

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

  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  public UserAccountType getUserAccountType() {
    return userAccountType;
  }
  public void setUserAccountType(UserAccountType userAccountType) {
    this.userAccountType = userAccountType;
  }
  public String getBirthDate() {
    return birthDate;
  }
  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }
  public String getProfilePicturePath() {
    return profilePicturePath;
  }
  public void setProfilePicturePath(String profilePicturePath) {
    this.profilePicturePath = profilePicturePath;
  }
  public String getPhoneNumber() {
    return phoneNumber;
  }
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  public String getUserBio() {
    return userBio;
  }
  public void setUserBio(String userBio) {
    this.userBio = userBio;
  } 

  public Car getCar() {
    return car;
  }
  public void setCar(Car car) {
    this.car = car;
  }
}

