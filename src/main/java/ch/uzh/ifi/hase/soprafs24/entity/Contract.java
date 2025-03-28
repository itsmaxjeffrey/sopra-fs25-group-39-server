package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;

import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    @Column
    private Location fromAddress;

    @Column
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

    @Column
    private Offer acceptedOffer;
    

    
}
