package ch.uzh.ifi.hase.soprafs24.rest.dto.offer;

import java.time.LocalDateTime;

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.responseDTO.AuthenticatedDriverDTO;
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