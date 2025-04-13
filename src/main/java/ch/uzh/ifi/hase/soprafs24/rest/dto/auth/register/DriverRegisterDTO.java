package ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DriverRegisterDTO extends BaseUserRegisterDTO {
    // Driver specific fields
    private String driverLicensePath;
    private String driverInsurancePath;
    private String carPicturePath;
    private String carModel;
    private float volumeCapacity;
    private float weightCapacity;
    private boolean electric;
    private String licensePlate;
    private float preferredRange; 

    // Add this constructor
    public DriverRegisterDTO() {
        this.setUserAccountType(UserAccountType.DRIVER);
    }
}

