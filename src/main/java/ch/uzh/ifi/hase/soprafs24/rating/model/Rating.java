package ch.uzh.ifi.hase.soprafs24.rating.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ch.uzh.ifi.hase.soprafs24.contract.model.Contract;
import ch.uzh.ifi.hase.soprafs24.user.model.User;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "RATING")
@Getter @Setter
public class Rating implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long ratingId;

    @ManyToOne
    @JoinColumn(name="from_user_id", nullable =false)
    private User fromUser;

    @ManyToOne
    @JoinColumn(name="to_user_id", nullable= false)
    private User toUser;

    @ManyToOne
    @JoinColumn(name="contract_id", nullable= false)
    private Contract contract;

    @Column(nullable = true)
    private Integer ratingValue;

    @Column(nullable = true)
    private boolean flagIssues;

    @Column(nullable = true)
    private String comment;



}