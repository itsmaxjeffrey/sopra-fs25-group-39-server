package ch.uzh.ifi.hase.soprafs24.rest.dto.contract;

import java.time.LocalDateTime;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;

public class ContractPostDTO {
    private String title;
    private float mass;
    private float height; // Renamed from volume
    private float width; // New field
    private float length; // New field
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
    private List<String> contractPhotos;

    private String imagePath1;
    private String imagePath2;
    private String imagePath3;

    // Getters and Setters

    public String getImagePath1() {
        return imagePath1;
    }

    public void setImagePath1(String imagePath1) {
        this.imagePath1 = imagePath1;
    }

    public String getImagePath2() {
        return imagePath2;
    }

    public void setImagePath2(String imagePath2) {
        this.imagePath2 = imagePath2;
    }

    public String getImagePath3() {
        return imagePath3;
    }

    public void setImagePath3(String imagePath3) {
        this.imagePath3 = imagePath3;
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

    public float getHeight() { // Renamed from getVolume
        return height;
    }

    public void setHeight(float height) { // Renamed from setVolume
        this.height = height;
    }

    public float getWidth() { // New getter
        return width;
    }

    public void setWidth(float width) { // New setter
        this.width = width;
    }

    public float getLength() { // New getter
        return length;
    }

    public void setLength(float length) { // New setter
        this.length = length;
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

    public List<String> getContractPhotos() {
        return contractPhotos;
    }

    public void setContractPhotos(List<String> contractPhotos) {
        this.contractPhotos = contractPhotos;
    }
}