package ch.uzh.ifi.hase.soprafs24.user.dto.request.update;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    property = "userAccountType",
    include = As.EXISTING_PROPERTY  // Use the userAccountType property for type info
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DriverUpdateDTO.class, name = "DRIVER"),
    @JsonSubTypes.Type(value = RequesterUpdateDTO.class, name = "REQUESTER")
})
public class BaseUserUpdateDTO {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthDate;
    private String profilePicturePath;
    private UserAccountType userAccountType;
}