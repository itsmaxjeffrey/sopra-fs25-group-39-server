package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Car;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
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


}

