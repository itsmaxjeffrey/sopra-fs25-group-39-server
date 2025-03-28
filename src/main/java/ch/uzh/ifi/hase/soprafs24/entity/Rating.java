package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;


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
    @JoinColumn(name="contractId", nullable= false)
    private Contract contract;

    @Column(nullable = true)
    private int ratingValue;

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
    public int getRatingValue() {
        return ratingValue;
    }
    public void setRatingValue(int ratingValue) {
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