package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="LOCATION")
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
    

    public String getFormattedAddress(){
        return this.formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress){
        this.formattedAddress = formattedAddress;
    }

    public Double getLatitude(){
        return this.latitude;
    }

    public void setLatitude(Double latitude){
        this.latitude = latitude;
    }

    public Double getLongitude(){
        return this.longitude;
    }

    public void setLongitude(Double longitude){
        this.longitude = longitude;
    }    

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}
