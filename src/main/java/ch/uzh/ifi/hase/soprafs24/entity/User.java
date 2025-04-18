package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "USERS")
@Getter @Setter
@EqualsAndHashCode
@ToString
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long userId;

  
  @Column(nullable = false, unique = true)
  private String username;

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

  @OneToMany(mappedBy = "fromUser")
  private List<Rating> ratingsGiven =  new ArrayList<>();

  @OneToMany(mappedBy = "toUser")
  private List<Rating> ratingsReceived =  new ArrayList<>();



  @Column(nullable = false)
  private String firstName;

  @Column(nullable=false)
  private String lastName;

  
  @Column(nullable=false, unique = true)
  private String phoneNumber;

  @Column(nullable=true)
  private String userBio;

  @Column(nullable=true)
  private String token;


  



  //ratings given
  public void addRatingGiven(Rating rating){
    this.ratingsGiven.add(rating);
    rating.setFromUser(this);
  }

  //ratings received
  public void addRatingReceived(Rating rating){
    this.ratingsReceived.add(rating);
    rating.setToUser(this);
  }

}
