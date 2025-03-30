package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import java.time.LocalDateTime;

public class ContractPostDTO {
    private String title;
    private float mass;
    private float volume;
    private boolean isFragile;
    private boolean coolingRequired;
    private boolean rideAlong;
    private int manPower;
    private String contractDescription;
    private float price;
    private float collateral;
    private Long requesterId;
    private LocationDTO fromLocation;
    private LocationDTO toLocation;
    private LocalDateTime moveDateTime;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public boolean getIsFragile() {
        return isFragile;
    }

    public void setIsFragile(boolean isFragile) {
        this.isFragile = isFragile;
    }

    public boolean getCoolingRequired() {
        return coolingRequired;
    }

    public void setCoolingRequired(boolean coolingRequired) {
        this.coolingRequired = coolingRequired;
    }

    public boolean getRideAlong() {
        return rideAlong;
    }

    public void setRideAlong(boolean rideAlong) {
        this.rideAlong = rideAlong;
    }

    public int getManPower() {
        return manPower;
    }

    public void setManPower(int manPower) {
        this.manPower = manPower;
    }

    public String getContractDescription() {
        return contractDescription;
    }

    public void setContractDescription(String contractDescription) {
        this.contractDescription = contractDescription;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCollateral() {
        return collateral;
    }

    public void setCollateral(float collateral) {
        this.collateral = collateral;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
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
}