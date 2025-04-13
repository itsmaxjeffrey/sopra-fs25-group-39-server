package ch.uzh.ifi.hase.soprafs24.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LocationDTO {

    private Long id;
    private String formattedAddress;
    private Double latitude;
    private Double longitude;

}