package ch.uzh.ifi.hase.soprafs24.rest.dto.contract;

import java.time.LocalDateTime;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;

public class ContractPutDTO {
    private String title;
    private Float mass;
    private Float volume;
    private Boolean fragile;
    private Boolean coolingRequired;
    private Boolean rideAlong;
    private Integer manPower;
    private String contractDescription;
    private Float price;
    private Float collateral;
    private LocationDTO fromLocation;
    private LocationDTO toLocation;
    private LocalDateTime moveDateTime;
    private ContractStatus contractStatus;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getMass() {
        return mass;
    }

    public void setMass(Float mass) {
        this.mass = mass;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public Boolean getFragile() {
        return fragile;
    }

    public void setFragile(Boolean fragile) {
        this.fragile = fragile;
    }

    public Boolean getCoolingRequired() {
        return coolingRequired;
    }

    public void setCoolingRequired(Boolean coolingRequired) {
        this.coolingRequired = coolingRequired;
    }

    public Boolean getRideAlong() {
        return rideAlong;
    }

    public void setRideAlong(Boolean rideAlong) {
        this.rideAlong = rideAlong;
    }

    public Integer getManPower() {
        return manPower;
    }

    public void setManPower(Integer manPower) {
        this.manPower = manPower;
    }

    public String getContractDescription() {
        return contractDescription;
    }

    public void setContractDescription(String contractDescription) {
        this.contractDescription = contractDescription;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getCollateral() {
        return collateral;
    }

    public void setCollateral(Float collateral) {
        this.collateral = collateral;
    }

    public LocationDTO getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(LocationDTO fromLocation) {
        this.fromLocation = fromLocation;
    }

    public LocationDTO getToLocation() {
        return toLocation;
    }

    public void setToLocation(LocationDTO toLocation) {
        this.toLocation = toLocation;
    }

    public LocalDateTime getMoveDateTime() {
        return moveDateTime;
    }

    public void setMoveDateTime(LocalDateTime moveDateTime) {
        this.moveDateTime = moveDateTime;
    }

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
    }
} 