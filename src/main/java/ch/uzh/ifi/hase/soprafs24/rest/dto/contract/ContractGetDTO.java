package ch.uzh.ifi.hase.soprafs24.rest.dto.contract;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ContractGetDTO {
    private Long contractId;
    private String title;
    private double weight;
    private double height;
    private double width;
    private double length;
    private boolean fragile;
    private boolean coolingRequired;
    private boolean rideAlong;
    private int manPower;
    private String contractDescription;
    private double price;
    // private double collateral;
    private Long requesterId;
    @JsonProperty("fromLocation")
    private LocationDTO fromLocation;
    @JsonProperty("toLocation")
    private LocationDTO toLocation;
    private LocalDateTime moveDateTime;
    private ContractStatus contractStatus;
    private LocalDateTime creationDateTime;
    private List<String> contractPhotos;
    private String cancelReason;
    private Long driverId;

}