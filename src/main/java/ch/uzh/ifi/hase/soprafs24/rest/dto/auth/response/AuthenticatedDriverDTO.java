package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response;

import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthenticatedDriverDTO extends AuthenticatedUserDTO {
    private String driverLicensePath;
    private String driverInsurancePath;
    private float preferredRange;
    private CarDTO car;
    private LocationDTO location;
}