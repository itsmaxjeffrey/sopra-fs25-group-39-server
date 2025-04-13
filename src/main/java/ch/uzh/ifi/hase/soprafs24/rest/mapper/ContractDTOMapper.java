package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPutDTO;

/**
 * ContractDTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation of a Contract entity to the external/API
 * representation (ContractGetDTO for getting, ContractPostDTO for creating)
 * and vice versa.
 */
@Mapper
public interface ContractDTOMapper {

    ContractDTOMapper INSTANCE = Mappers.getMapper(ContractDTOMapper.class);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "mass", target = "mass")
    @Mapping(source = "volume", target = "volume")
    @Mapping(source = "fragile", target = "fragile")
    @Mapping(source = "coolingRequired", target = "coolingRequired")
    @Mapping(source = "rideAlong", target = "rideAlong")
    @Mapping(source = "manPower", target = "manPower")
    @Mapping(source = "contractDescription", target = "contractDescription")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "collateral", target = "collateral")
    @Mapping(source = "moveDateTime", target = "moveDateTime")
    @Mapping(source = "requesterId", target = "requester")
    @Mapping(source = "contractPhotos", target = "contractPhotos")
    @Mapping(target = "contractId", ignore = true)
    @Mapping(target = "contractStatus", ignore = true)
    @Mapping(target = "creationDateTime", ignore = true)
    @Mapping(target = "acceptedDateTime", ignore = true)
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "acceptedOffer", ignore = true)
    @Mapping(target = "fromAddress", ignore = true)
    @Mapping(target = "toAddress", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    @Mapping(target = "driver", ignore = true)
    Contract convertContractPostDTOtoEntity(ContractPostDTO contractPostDTO);

    @Mapping(source = "contractId", target = "contractId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "mass", target = "mass")
    @Mapping(source = "volume", target = "volume")
    @Mapping(source = "fragile", target = "fragile")
    @Mapping(source = "coolingRequired", target = "coolingRequired")
    @Mapping(source = "rideAlong", target = "rideAlong")
    @Mapping(source = "manPower", target = "manPower")
    @Mapping(source = "contractDescription", target = "contractDescription")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "collateral", target = "collateral")
    @Mapping(source = "moveDateTime", target = "moveDateTime")
    @Mapping(source = "contractStatus", target = "contractStatus")
    @Mapping(source = "creationDateTime", target = "creationDateTime")
    @Mapping(source = "contractPhotos", target = "contractPhotos")
    @Mapping(source = "requester.userId", target = "requesterId")
    @Mapping(source = "fromAddress", target = "fromLocation")
    @Mapping(source = "toAddress", target = "toLocation")
    @Mapping(source = "cancelReason", target = "cancelReason")
    ContractGetDTO convertContractEntityToContractGetDTO(Contract contract);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "mass", target = "mass")
    @Mapping(source = "volume", target = "volume")
    @Mapping(source = "fragile", target = "fragile")
    @Mapping(source = "coolingRequired", target = "coolingRequired")
    @Mapping(source = "rideAlong", target = "rideAlong")
    @Mapping(source = "manPower", target = "manPower")
    @Mapping(source = "contractDescription", target = "contractDescription")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "collateral", target = "collateral")
    @Mapping(source = "moveDateTime", target = "moveDateTime")
    @Mapping(source = "contractStatus", target = "contractStatus")
    @Mapping(target = "contractId", ignore = true)
    @Mapping(target = "creationDateTime", ignore = true)
    @Mapping(target = "acceptedDateTime", ignore = true)
    @Mapping(target = "contractPhotos", ignore = true)
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "acceptedOffer", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "fromAddress", ignore = true)
    @Mapping(target = "toAddress", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    @Mapping(target = "driver", ignore = true)
    Contract convertContractPutDTOtoEntity(ContractPutDTO contractPutDTO);

    default Requester map(Long requesterId) {
        if (requesterId == null) {
            return null;
        }
        Requester requester = new Requester();
        requester.setUserId(requesterId);
        return requester;
    }
} 