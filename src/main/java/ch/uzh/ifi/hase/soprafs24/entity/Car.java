package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CARS")
@Getter @Setter
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long carId;


    @Column
    private String carModel;

    @Column
    private float volumeCapacity;

    @Column
    private float weightCapacity;

    @Column
    private boolean electric;

    @Column
    private String licensePlate;

    @Column
    private String carPicturePath;

    @OneToOne(mappedBy = "car")
    private Driver driver;


}
