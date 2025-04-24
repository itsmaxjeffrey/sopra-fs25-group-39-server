package ch.uzh.ifi.hase.soprafs24.rest.dto.contract;

import java.time.LocalDateTime;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ContractPostDTO {
    private String title;
    private double weight;
    private double height; // Renamed from volume
    private double width; // New field
    private double length; // New field
    private boolean fragile;
    private boolean coolingRequired;
    private boolean rideAlong;
    private int manPower;
    private String contractDescription;
    private double price;
    private double collateral;
    private Long requesterId;
    private LocationDTO fromLocation;
    private LocationDTO toLocation;
    private LocalDateTime moveDateTime;
    private List<String> contractPhotos;

    private String imagePath1;
    private String imagePath2;
    private String imagePath3;


}