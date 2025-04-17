package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Offer;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedDriverDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPutDTO;

/**
 * OfferDTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation of an Offer entity to the external/API
 * representation (OfferGetDTO for getting)
 * and vice versa.
 */

@Mapper(componentModel = "spring", uses = {ContractDTOMapper.class})
public interface OfferDTOMapper {

    OfferDTOMapper INSTANCE = Mappers.getMapper(OfferDTOMapper.class);

    @Mapping(source = "offerId", target = "offerId")
    @Mapping(source = "contract", target = "contract")
    @Mapping(source = "driver", target = "driver")
    @Mapping(source = "offerStatus", target = "offerStatus")
    @Mapping(source = "creationDateTime", target = "creationDateTime")
    OfferGetDTO convertEntityToOfferGetDTO(Offer offer);

    @Mapping(target = "offerId", ignore = true)
    @Mapping(target = "contract", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "offerStatus", ignore = true)
    @Mapping(target = "creationDateTime", ignore = true)
    Offer convertOfferPostDTOtoEntity(OfferPostDTO offerPostDTO);

    @Mapping(target = "offerId", ignore = true)
    @Mapping(target = "contract", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "creationDateTime", ignore = true)
    @Mapping(source = "status", target = "offerStatus")
    Offer convertOfferPutDTOtoEntity(OfferPutDTO offerPutDTO);

    /**
     * Custom mapping method for converting Driver to AuthenticatedDriverDTO
     */
    default AuthenticatedDriverDTO mapDriver(Driver driver) {
        if (driver == null) {
            return null;
        }
        return (AuthenticatedDriverDTO) new UserDTOMapper().convertToDTO(driver);
    }
} 