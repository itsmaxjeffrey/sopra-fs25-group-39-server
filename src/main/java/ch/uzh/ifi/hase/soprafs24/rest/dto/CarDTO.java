package ch.uzh.ifi.hase.soprafs24.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CarDTO {

    private String carModel;
    private float space;
    private float supportedWeight;
    private boolean isElectric;
    private String licensePlate;


}