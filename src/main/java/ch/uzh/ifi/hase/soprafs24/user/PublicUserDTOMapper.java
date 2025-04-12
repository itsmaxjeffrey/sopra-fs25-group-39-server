package ch.uzh.ifi.hase.soprafs24.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs24.entity.User;

@Mapper(componentModel = "spring")
public interface PublicUserDTOMapper {
    PublicUserDTOMapper INSTANCE = Mappers.getMapper(PublicUserDTOMapper.class);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "userAccountType", target = "userAccountType")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "profilePicturePath", target = "profilePicturePath")
    PublicUserDTO convertToPublicUserDTO(User user);

}
