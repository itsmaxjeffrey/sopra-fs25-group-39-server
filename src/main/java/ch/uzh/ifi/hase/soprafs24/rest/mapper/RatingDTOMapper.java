package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RatingDTOMapper {

    RatingDTOMapper INSTANCE = Mappers.getMapper(RatingDTOMapper.class);

    @Mapping(source = "fromUser.userId", target = "fromUserId")
    @Mapping(source = "toUser.userId", target = "toUserId")
    @Mapping(source = "contract.contractId", target = "contractId")
    RatingDTO convertEntityToRatingDTO(Rating rating);
}
