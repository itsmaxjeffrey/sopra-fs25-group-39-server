package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "RATING")
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

    public Long getRatingId() {
        return ratingId;
    }
    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }
    public User getFromUser() {
        return fromUser;
    }
    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
    public User getToUser() {
        return toUser;
    }
    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
    public Contract getContract() {
        return contract;
    }
    public void setContract(Contract contract) {
        this.contract = contract;
    }
    public Integer getRatingValue() {
        return ratingValue;
    }
    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }
    public boolean isFlagIssues() {
        return flagIssues;
    }
    public void setFlagIssues(boolean flagIssues) {
        this.flagIssues = flagIssues;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }


}