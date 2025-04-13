package ch.uzh.ifi.hase.soprafs24.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CarDTO {

    private Long carId;
    private String carModel;
    private float volumeCapacity;
    private float weightCapacity;
    private boolean electric;
    private String licensePlate;
    private String carPicturePath;
    private Long driverId;
}