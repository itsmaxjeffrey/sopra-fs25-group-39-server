package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CARS")
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long carId;
    
    public Long getCarId() {
        return this.carId;
    }
    
    public void setCarId(Long carId) {
        this.carId = carId;
    }


    @Column
    private String carModel;

    @Column
    private float space;

    @Column
    private float supportedWeight;

    @Column
    private boolean electric;

    @Column
    private String licensePlate;

    @Column
    private String carPicturePath;

    @OneToOne(mappedBy = "car")
    private Driver driver;

    public String getCarModel() {
        return this.carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public float getSpace() {
        return this.space;
    }

    public void setSpace(float space) {
        this.space = space;
    }

    public float getSupportedWeight() {
        return this.supportedWeight;
    }
    public void setSupportedWeight(float supportedWeight) {
        this.supportedWeight = supportedWeight;
    }
    public boolean isElectric() {
        return this.electric;
    }
    public void setElectric(boolean electric) {
        this.electric = electric;
    }
    public String getLicensePlate() {
        return this.licensePlate;
    }
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    public String getCarPicturePath() {
        return this.carPicturePath;
    }
    public void setCarPicturePath(String carPicturePath) {
        this.carPicturePath = carPicturePath;
    }
    public Driver getDriver() {
        return this.driver;
    }
    public void setDriver(Driver driver) {
        this.driver = driver;
    }


}
