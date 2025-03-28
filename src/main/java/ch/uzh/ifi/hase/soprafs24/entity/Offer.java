package ch.uzh.ifi.hase.soprafs24.entity;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import org.hibernate.annotations.CreationTimestamp;

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;

@Entity
@Table(name = "OFFERS")
public class Offer implements Serializable {
    private static final long SerialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long offerId;


    @ManyToOne
    @JoinColumn(name = "contractId")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "driverId")
    private Driver driver;

    @Column
    private OfferStatus offerStatus;

    @CreationTimestamp
    @Column
    private LocalDateTime creationDateTime;


    
}
