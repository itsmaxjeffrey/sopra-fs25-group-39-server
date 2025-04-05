package ch.uzh.ifi.hase.soprafs24.rest.dto;

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
}

