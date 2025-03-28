package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name ="DRIVERS")
public class Driver extends User {

    @Column(nullable = false)
    private String driverLicencePath;

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

}
