package ch.uzh.ifi.hase.soprafs24.offer.dto.response;

import java.time.LocalDateTime;

import ch.uzh.ifi.hase.soprafs24.common.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.contract.dto.response.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedDriverDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferGetDTO {
    private Long offerId;
    private ContractGetDTO contract;
    private AuthenticatedDriverDTO driver;
    private OfferStatus offerStatus;
    private LocalDateTime creationDateTime;
} 