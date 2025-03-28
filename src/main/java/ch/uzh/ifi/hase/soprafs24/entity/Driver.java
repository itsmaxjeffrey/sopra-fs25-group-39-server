package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name ="DRIVERS")
public class Driver extends User {

    @Column(nullable = false)
    private String driverLicensePath;

    @Column
    private String driverInsurancePath;

    @OneToOne( orphanRemoval = true)
    @JoinColumn(name="car_id", referencedColumnName = "id", nullable = false)
    private Car car;

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="location_id", referencedColumnName = "id")
    private Location location;

    @Column
    private float preferredRange;

    public String getDriverLicensePath(){
        return this.driverLicensePath;
    }

    public void setDriverLicensePath(String driverLicensePath){
        this.driverLicensePath = driverLicensePath;
    }

    public String getDriverInsurancePath(){
        return this.driverInsurancePath;
    }

    public void setDriverInsurancePath(String driverInsurancePath){
        this.driverInsurancePath = driverInsurancePath;
    }
    
    public Car getCar(){
        return this.car;
    }

    public void setCar(Car car){
        this.car = car;
    }

    public Location getLocation(){
        return this.location;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public float getPreferredRange(){
        return this.preferredRange;
    }

    public void getPreferredRange(float preferredRange){
        this.preferredRange= preferredRange;
    }
}
