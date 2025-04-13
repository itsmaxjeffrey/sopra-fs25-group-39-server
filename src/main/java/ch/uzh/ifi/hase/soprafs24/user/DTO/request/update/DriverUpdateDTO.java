package ch.uzh.ifi.hase.soprafs24.user.dto.request.update;

import ch.uzh.ifi.hase.soprafs24.car.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.common.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.location.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DriverUpdateDTO extends BaseUserUpdateDTO {
    private String driverLicensePath;
    private String driverInsurancePath;
    private float preferredRange;
    private CarDTO car;
    private LocationDTO location;
    
    public DriverUpdateDTO() {
        this.setUserAccountType(UserAccountType.DRIVER);
    }
}