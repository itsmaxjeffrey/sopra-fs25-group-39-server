package ch.uzh.ifi.hase.soprafs24.user.dto.request.update;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DriverUpdateDTO extends BaseUserUpdateDTO {
    private String driverLicensePath;
    private String driverInsurancePath;
    private float preferredRange;
    private CarDTO car;
    private LocationDTO location;
    private UserAccountType userAccountType;
    
    public DriverUpdateDTO() {
        this.userAccountType = UserAccountType.DRIVER;
    }
}