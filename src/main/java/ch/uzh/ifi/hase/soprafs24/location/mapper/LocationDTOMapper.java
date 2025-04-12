package ch.uzh.ifi.hase.soprafs24.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs24.location.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.location.model.Location;

/**
 * LocationDTOMapper
 * This class is responsible for mapping between Location entity and LocationDTO
 */
@Mapper(componentModel = "spring")
public interface LocationDTOMapper {

    LocationDTOMapper INSTANCE = Mappers.getMapper(LocationDTOMapper.class);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "latitude", target = "latitude")
    @Mapping(source = "longitude", target = "longitude")
    @Mapping(source = "formattedAddress", target = "formattedAddress")
    LocationDTO convertEntityToLocationDTO(Location location);

    @Mapping(source = "latitude", target = "latitude")
    @Mapping(source = "longitude", target = "longitude")
    @Mapping(source = "formattedAddress", target = "formattedAddress")
    Location convertLocationDTOToEntity(LocationDTO locationDTO);
}