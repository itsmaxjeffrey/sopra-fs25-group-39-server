package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long userId;
  
  @Column(nullable = false, unique = true)
  private String userName;

  @Column(nullable = false)
  private String password;

  
  @Column(nullable=false, unique = true)
  private String email;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserAccountType userAccountType;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime creationDate;

  @Column(nullable = true)
  private LocalDate birthDate;

  @Column(nullable=true)
  private String profilePicturePath;

  @Column(nullable = false)
  private Double walletBalance = 0.0;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Rating> ratings =  new ArrayList<>();


  @Column(nullable = false)
  private String firstName;

  @Column(nullable=false)
  private String lastName;

  
  @Column(nullable=false, unique = true)
  private String phoneNumber;

  @Column(nullable=true)
  private String userBio;


  



  //id
  public Long getId() {
    return userId;
  }

  public void setId(Long userId) {
    this.userId = userId;
  }


  //username
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

   //password
   public void setPassword(String password){
    this.password = password;
  }
  public String getPassword(){
    return this.password;
  }


   //email
   public void setEmail(String email){
    this.email = email;
  }
  public String getEmail(){
    return this.email;
  }


  //account type
  public void setUserAccountType(UserAccountType userAccountType){
    this.userAccountType = userAccountType;
  }
  public UserAccountType getUserAccountType(){
    return this.userAccountType;
  }


  //user creation LocalDateTime

  public LocalDateTime getCreationDate(){
    return this.creationDate;
  }


  //birthDate

  public LocalDate getBirthDate(){
    return this.birthDate;
  }

  public void setBirthDate(LocalDate birthDate){
    this.birthDate = birthDate;
  }

  //profile picture path 

  public String getProfilePicturePath(){
    return this.profilePicturePath;
  }

  public void setProfilePicturePath(String profilePicturePath){
    this.profilePicturePath= profilePicturePath;
  }

  //wallet balance

  public Double getWalletBalance(){
    return this.walletBalance;
  }
  public void setWalletBalance(Double walletBalance){
    this.walletBalance = walletBalance;
  }


  //first name
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  //last name
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  //ratings
  public void setRatings(List<Rating> ratings){
    this.ratings = ratings;
  }
  public void addRating(Rating rating){
    this.ratings.add(rating);
    rating.setUser(this);
  }

  public void setPhoneNumber(String phoneNumber){
    this.phoneNumber = phoneNumber;
  }
  public String getPhoneNumber(){
    return this.phoneNumber;
  }
 


}
