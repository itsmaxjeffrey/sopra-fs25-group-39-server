package ch.uzh.ifi.hase.soprafs24.rest.dto.contract;

import java.time.LocalDateTime;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;

public class ContractPutDTO {
    private String title;
    private float mass;
    private float volume;
    private boolean fragile;
    private boolean coolingRequired;
    private boolean rideAlong;
    private int manPower;
    private String contractDescription;
    private float price;
    private float collateral;
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

    public boolean getFragile() {
        return fragile;
    }

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
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