package ch.uzh.ifi.hase.soprafs24.rest.dto.contract;

import java.time.LocalDateTime;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContractPutDTO {
    private String title;
    private double weight;
    private double height;
    private double width;
    private double length;
    private Boolean fragile;
    private Boolean coolingRequired;
    private Boolean rideAlong;
    private Integer manPower;
    private String contractDescription;
    private double price;
    private double collateral;
    private LocationDTO fromLocation;
    private LocationDTO toLocation;
    private LocalDateTime moveDateTime;
    private ContractStatus contractStatus;

}