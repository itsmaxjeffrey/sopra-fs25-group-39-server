package ch.uzh.ifi.hase.soprafs24.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.BaseUserUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.DriverUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.RequesterUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.model.Driver;
import ch.uzh.ifi.hase.soprafs24.user.model.Requester;
import ch.uzh.ifi.hase.soprafs24.user.model.User;


@Mapper(componentModel = "spring")
public interface UserUpdateDTOMapper {
    
    UserUpdateDTOMapper INSTANCE = Mappers.getMapper(UserUpdateDTOMapper.class);
    
    // Generic method to handle polymorphic user types
    default User convertToEntity(BaseUserUpdateDTO updateDTO) {
        if (updateDTO instanceof DriverUpdateDTO) {
            return convertToDriverEntity((DriverUpdateDTO) updateDTO);
        } else if (updateDTO instanceof RequesterUpdateDTO) {
            return convertToRequesterEntity((RequesterUpdateDTO) updateDTO);
        } else {
            return convertToBaseUserEntity(updateDTO);
        }
    }
    
    // Base user mapping
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "profilePicturePath", target = "profilePicturePath")
    @Mapping(source = "userAccountType", target = "userAccountType")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "walletBalance", ignore = true)
    @Mapping(target = "ratingsGiven", ignore = true)
    @Mapping(target = "ratingsReceived", ignore = true)
    User convertToBaseUserEntity(BaseUserUpdateDTO updateDTO);
    
    // Driver mapping
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName") 
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "profilePicturePath", target = "profilePicturePath")
    @Mapping(source = "userAccountType", target = "userAccountType")
    @Mapping(source = "driverLicensePath", target = "driverLicensePath")
    @Mapping(source = "driverInsurancePath", target = "driverInsurancePath")
    @Mapping(source = "preferredRange", target = "preferredRange")
    @Mapping(target = "car", ignore = true) // Handle car separately in service
    @Mapping(target = "location", ignore = true) // Handle location separately in service
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "token", ignore = true) 
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "walletBalance", ignore = true)
    @Mapping(target = "ratingsGiven", ignore = true)
    @Mapping(target = "ratingsReceived", ignore = true)
    Driver convertToDriverEntity(DriverUpdateDTO updateDTO);
    
    // Requester mapping
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email") 
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "profilePicturePath", target = "profilePicturePath")
    @Mapping(source = "userAccountType", target = "userAccountType")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "creationDate", ignore = true) 
    @Mapping(target = "walletBalance", ignore = true)
    @Mapping(target = "ratingsGiven", ignore = true)
    @Mapping(target = "ratingsReceived", ignore = true)
    // Add any requester-specific mappings here if needed
    Requester convertToRequesterEntity(RequesterUpdateDTO updateDTO);
}