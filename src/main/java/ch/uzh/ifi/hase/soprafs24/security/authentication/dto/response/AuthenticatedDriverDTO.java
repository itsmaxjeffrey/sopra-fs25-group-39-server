package ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response;

import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthenticatedDriverDTO extends AuthenticatedUserDTO {
    private String driverLicensePath;
    private String driverInsurancePath;
    private float preferredRange;
    private LocationDTO location;
    private CarDTO carDTO;

}