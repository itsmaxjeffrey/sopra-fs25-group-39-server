package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferPutDTO {
    private OfferStatus status;
} 