package ch.uzh.ifi.hase.soprafs24.offer.model;
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

import ch.uzh.ifi.hase.soprafs24.common.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.contract.model.Contract;
import ch.uzh.ifi.hase.soprafs24.user.model.Driver;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OFFERS")
@Getter @Setter
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

}
