package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Offer;
import ch.uzh.ifi.hase.soprafs24.rest.dto.OfferGetDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * OfferDTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation of an Offer entity to the external/API
 * representation (OfferGetDTO for getting)
 * and vice versa.
 */
@Mapper(uses = {ContractDTOMapper.class, DTOMapper.class})
public interface OfferDTOMapper {

    OfferDTOMapper INSTANCE = Mappers.getMapper(OfferDTOMapper.class);

    @Mapping(source = "offerId", target = "offerId")
    @Mapping(source = "contract", target = "contract")
    @Mapping(source = "driver", target = "driver")
    @Mapping(source = "offerStatus", target = "offerStatus")
    @Mapping(source = "creationDateTime", target = "creationDateTime")
    OfferGetDTO convertEntityToOfferGetDTO(Offer offer);
} 