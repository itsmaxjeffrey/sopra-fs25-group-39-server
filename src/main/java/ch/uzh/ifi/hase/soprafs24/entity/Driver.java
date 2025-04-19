package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name ="DRIVERS")
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Driver extends User {

    @Column(nullable = true)
    private String driverLicensePath;

    @Column(nullable = true)
    private String driverInsurancePath;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name="car_id", referencedColumnName = "carId", nullable = false)
    private Car car;

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="location_id", referencedColumnName = "id")
    private Location location;

    @Column
    private float preferredRange;


}
