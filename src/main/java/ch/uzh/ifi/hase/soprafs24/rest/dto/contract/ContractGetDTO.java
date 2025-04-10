package ch.uzh.ifi.hase.soprafs24.rest.dto.contract;

import java.time.LocalDateTime;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;

public class ContractGetDTO {
    private Long contractId;
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
    private Long requesterId;
    private LocationDTO fromLocation;
    private LocationDTO toLocation;
    private LocalDateTime moveDateTime;
    private ContractStatus contractStatus;
    private LocalDateTime creationDateTime;
    private List<String> contractPhotos;
    private String cancelReason;

    // Getters and Setters
    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

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

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<String> getContractPhotos() {
        return contractPhotos;
    }

    public void setContractPhotos(List<String> contractPhotos) {
        this.contractPhotos = contractPhotos;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}