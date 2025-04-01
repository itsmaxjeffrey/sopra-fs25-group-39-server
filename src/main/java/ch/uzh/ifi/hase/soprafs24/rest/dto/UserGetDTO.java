package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
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



}
