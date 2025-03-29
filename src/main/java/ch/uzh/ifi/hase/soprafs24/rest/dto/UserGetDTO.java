package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Rating;

public class UserGetDTO {

  private Long userId;
  private String firstName;
  private String lastName;
  private String username;
  private UserAccountType userAccountType;
  private String profilePicturePath;
  private String phoneNumber;
  private String userBio;
  private String birthDate;
  private String email;
  private String password;
  private String creationDate;
  private Double walletBalance;
  private List<Rating> ratingsGiven;
  private List<Rating> ratingsReceived;
  private Car car;
  private List<Contract> contracts;

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
  public String getBirthDate() {
    return birthDate;
  }
  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getCreationDate() {
    return creationDate;
  }
  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }
  public Double getWalletBalance() {
    return walletBalance;
  }
  public void setWalletBalance(Double walletBalance) {
    this.walletBalance = walletBalance;
  }
  public List<Rating> getRatingsGiven() {
    return ratingsGiven;
  }
  public void setRatingsGiven(List<Rating> ratingsGiven) {
    this.ratingsGiven = ratingsGiven;
  }
  public List<Rating> getRatingsReceived() {
    return ratingsReceived;
  }
  public void setRatingsReceived(List<Rating> ratingsReceived) {
    this.ratingsReceived = ratingsReceived;
  }

  public Car getCar() {
    return car;
  }
  public void setCar(Car car) {
    this.car = car;
  }

  public List<Contract> getContracts() {
    return contracts;
  }

  public void setContracts(List<Contract> contracts) {
    this.contracts = contracts;
  }


}
