package ch.uzh.ifi.hase.soprafs24.security.registration.dto;

import ch.uzh.ifi.hase.soprafs24.common.constant.UserAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DriverRegisterDTO extends BaseUserRegisterDTO {
    // Driver specific fields
    private String driverLicensePath;
    private String driverInsurancePath;
    private String carPicturePath;
    private String carModel;
    private float space;
    private float supportedWeight;
    private boolean electric;
    private String licensePlate;
    private float preferredRange; 

    // Add this constructor
    public DriverRegisterDTO() {
        this.setUserAccountType(UserAccountType.DRIVER);
    }
}

