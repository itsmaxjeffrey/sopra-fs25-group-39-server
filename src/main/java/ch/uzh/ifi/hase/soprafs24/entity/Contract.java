package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import javax.persistence.ManyToOne;

import java.io.Serializable;

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
    

    
}
