package ch.uzh.ifi.hase.soprafs24.security.registration.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import ch.uzh.ifi.hase.soprafs24.common.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    property = "userAccountType",
    include = As.EXISTING_PROPERTY  // This tells Jackson to also set the property
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DriverRegisterDTO.class, name = "DRIVER"),
    @JsonSubTypes.Type(value = RequesterRegisterDTO.class, name = "REQUESTER")
})
public class BaseUserRegisterDTO {
    // Common fields for all users
    private String username;
    private String password;
    private String email;
    private UserAccountType userAccountType;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthDate;
    private String profilePicturePath;
}