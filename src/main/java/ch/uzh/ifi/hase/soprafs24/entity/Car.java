package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CARS")
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;


    @Column
    private String carModel;

    @Column
    private float space;

    @Column
    private float supportedWeight;

    @Column
    private boolean isElectric;

    @Column
    private String licensePlate;

    @Column
    private String carPicturePath;

    @OneToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
}
