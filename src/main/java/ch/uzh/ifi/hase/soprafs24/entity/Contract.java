package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="CONTRACTS")
@EqualsAndHashCode
@ToString(exclude = "requester")
@Getter @Setter
public class Contract implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long contractId;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable= false)
    private Requester requester;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable= true)
    private Driver driver;

    @Column
    @Enumerated(EnumType.STRING)
    private ContractStatus contractStatus;

    @CreationTimestamp
    @Column
    private LocalDateTime creationDateTime;
    

    @Column
    private String title;

    @Column
    private LocalDateTime acceptedDateTime;

    @Column
    private LocalDateTime moveDateTime;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_address_id")
    private Location fromAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_address_id")
    private Location toAddress;
    
    @Column
    private double weight;

    @Column
    private double height;

    @Column
    private double width;

    @Column
    private double length;

    @Column
    private boolean fragile;
    
    @Column
    private boolean coolingRequired;

    @Column
    private boolean rideAlong;


    @Column
    private int manPower;

    @Column
    private String contractDescription;

    @ElementCollection
    @CollectionTable(name = "contract_photos", joinColumns = @JoinColumn(name = "contract_id"))
    @Column(name = "photo")
    private List<String> contractPhotos = new ArrayList<>();

    @Column
    private double price;

    // @Column
    // private double collateral;


    @OneToMany(mappedBy = "contract")
    private List<Offer> offers = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "accepted_offer_id")
    private Offer acceptedOffer;
    

    @Column(nullable = true)
    private String cancelReason;

    public void addContractPhoto(String photo) {
        this.contractPhotos.add(photo);
    }


    public void addOffer(Offer offer) {
        this.offers.add(offer);
        offer.setContract(this);
    }

}
