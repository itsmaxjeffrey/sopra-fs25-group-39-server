package ch.uzh.ifi.hase.soprafs24.entity;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;

@Entity
@Table(name = "OFFERS")
public class Offer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long offerId;


    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column
    private OfferStatus offerStatus;

    @CreationTimestamp
    @Column
    private LocalDateTime creationDateTime;


    public Contract getContract() {
    return this.contract;
    }

    public void setContract(Contract contract){
        this.contract = contract;
    }

    public Driver getDriver(){
        return this.driver;
    }
    
    public void setDriver(Driver driver){
        this.driver = driver;
    }

    public OfferStatus getOfferStatus(){
        return this.offerStatus;
    }

    public void setOfferStatus(OfferStatus offerStatus){
        this.offerStatus = offerStatus;
    }

    public LocalDateTime getCreationDateTime(){
        return this.creationDateTime;
    }
    
}
