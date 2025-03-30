package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class CarDTO {

    private String carModel;
    private float space;
    private float supportedWeight;
    private boolean isElectric;
    private String licensePlate;

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public float getSpace() {
        return space;
    }

    public void setSpace(float space) {
        this.space = space;
    }

    public float getSupportedWeight() {
        return supportedWeight;
    }

    public void setSupportedWeight(float supportedWeight) {
        this.supportedWeight = supportedWeight;
    }

    public boolean getIsElectric() {
        return isElectric;
    }

    public void setIsElectric(boolean isElectric) {
        this.isElectric = isElectric;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}