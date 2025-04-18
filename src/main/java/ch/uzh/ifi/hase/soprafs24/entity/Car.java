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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "CARS")
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long carId;

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
}
