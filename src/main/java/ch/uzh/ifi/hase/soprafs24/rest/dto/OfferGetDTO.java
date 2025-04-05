package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDateTime;

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferGetDTO {
    private Long offerId;
    private ContractGetDTO contract;
    private UserGetDTO driver;
    private OfferStatus offerStatus;
    private LocalDateTime creationDateTime;
} 