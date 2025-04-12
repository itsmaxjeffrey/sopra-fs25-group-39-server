package ch.uzh.ifi.hase.soprafs24.offer.dto.request;

import ch.uzh.ifi.hase.soprafs24.common.constant.OfferStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferPutDTO {
    private OfferStatus status;
} 