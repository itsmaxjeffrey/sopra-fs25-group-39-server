package ch.uzh.ifi.hase.soprafs24.location.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="LOCATION")
@Getter @Setter
public class Location implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String formattedAddress;

    @Column
    private Double latitude;
    
    @Column
    private Double longitude;
    

}
