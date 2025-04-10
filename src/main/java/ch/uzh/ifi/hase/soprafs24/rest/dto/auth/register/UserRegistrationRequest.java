package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register;

import com.fasterxml.jackson.annotation.JsonInclude;

import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegistrationRequest {
    private BaseUserRegisterDTO user; // This will be either DriverRegisterDTO or RequesterRegisterDTO
    private CarDTO car; // Optional, only needed for drivers
    private LocationDTO location; // Optional, only needed for drivers

}