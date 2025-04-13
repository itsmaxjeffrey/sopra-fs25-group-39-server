package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="DRIVERS")
@Getter @Setter
public class Driver extends User {

    @Column(nullable = false)
    private String driverLicensePath;

    @Column
    private String driverInsurancePath;

    @OneToOne( orphanRemoval = true)
    @JoinColumn(name="car_id", referencedColumnName = "carId", nullable = false)
    private Car car;

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="location_id", referencedColumnName = "id")
    private Location location;

    @Column
    private float preferredRange;


}
