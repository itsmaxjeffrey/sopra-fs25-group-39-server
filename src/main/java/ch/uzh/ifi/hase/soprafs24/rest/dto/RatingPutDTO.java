package ch.uzh.ifi.hase.soprafs24.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RatingPutDTO {
    private Integer ratingValue;
    private boolean flagIssues;
    private String comment;
} 