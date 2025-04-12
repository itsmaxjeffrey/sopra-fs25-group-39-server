package ch.uzh.ifi.hase.soprafs24.rating.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RatingDTO {
    private Long ratingId;
    private Long fromUserId;
    private Long toUserId;
    private Long contractId;
    private Integer ratingValue;
    private boolean flagIssues;
    private String comment;
}
