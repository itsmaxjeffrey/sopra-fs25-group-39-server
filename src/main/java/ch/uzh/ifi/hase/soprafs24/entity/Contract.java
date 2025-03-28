package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;

@Entity
@Table(name="CONTRACTS")
public class Contract implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long contractId;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable= false)
    private Requester requester;

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


    @ManyToOne
    @JoinColumn(name = "from_address_id")
    private Location fromAddress;

    @ManyToOne
    @JoinColumn(name = "to_address_id")
    private Location toAddress;
    
    @Column
    private float mass;

    @Column
    private float volume;

    @Column
    private boolean isFragile;
    
    @Column
    private boolean coolingRequired;

    @Column
    private boolean rideAlong;


    @Column
    private int manPower;

    @Column
    private String contractDescription;

    @Column
    private List<String> contractPhotos = new ArrayList<>();

    @Column
    private float price;

    @Column
    private float collateral;


    @OneToMany(mappedBy = "contract")
    private List<Offer> offers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "accepted_offer_id")
    
    public Requester getRequester() {
        return this.requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }

    public ContractStatus getContractStatus() {
        return this.contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
    }

    public LocalDateTime getCreationDateTime() {
        return this.creationDateTime;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getAcceptedDateTime() {
        return this.acceptedDateTime;
    }
    public LocalDateTime getMoveDateTime() {
        return this.moveDateTime;
    }


    public Location getFromAddress() {
        return this.fromAddress;
    }

    public void setFromAddress(Location fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Location getToAddress() {
        return this.toAddress;
    }

    public void setToAddress(Location toAddress) {
        this.toAddress = toAddress;
    }

    public float getMass() {
        return this.mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public boolean getIsFragile() {
        return this.isFragile;
    }

    public void setIsFragile(boolean isFragile) {
        this.isFragile = isFragile;
    }

    public boolean getCoolingRequired() {
        return this.coolingRequired;
    }

    public void setCoolingRequired(boolean coolingRequired) {
        this.coolingRequired = coolingRequired;
    }

    public boolean getRideAlong() {
        return this.rideAlong;
    }

    public void setRideAlong(boolean rideAlong) {
        this.rideAlong = rideAlong;
    }

    public int getManPower() {
        return this.manPower;
    }

    public void setManPower(int manPower) {
        this.manPower = manPower;
    }

    public String getContractDescription() {
        return this.contractDescription;
    }

    public void setContractDescription(String contractDescription) {
        this.contractDescription = contractDescription;
    }

    public List<String> getContractPhotos() {
        return this.contractPhotos;
    }

    public void setContractPhotos(List<String> contractPhotos) {
        this.contractPhotos = contractPhotos;
    }

    public void addContractPhoto(String photo) {
        this.contractPhotos.add(photo);
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCollateral() {
        return this.collateral;
    }

    public void setCollateral(float collateral) {
        this.collateral = collateral;
    }

    public List<Offer> getOffers() {
        return this.offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public void addOffer(Offer offer) {
        this.offers.add(offer);
    }

    public Offer getAcceptedOffer() {
        return this.acceptedOffer;
    }

    public void setAcceptedOffer(Offer acceptedOffer) {
        this.acceptedOffer = acceptedOffer;
    }
}
