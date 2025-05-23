package ch.uzh.ifi.hase.soprafs24.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.CarDTOMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.LocationDTOMapper;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedDriverDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedRequesterDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;

@Mapper(
    componentModel = "spring", 
    uses = {CarDTOMapper.class, LocationDTOMapper.class}
)public interface UserDTOMapper {
    
    UserDTOMapper INSTANCE = Mappers.getMapper(UserDTOMapper.class);
    
    // Use this method as the entry point for converting users
    default AuthenticatedUserDTO convertToDTO(User user) {
        if (user instanceof Driver driver) {
            return convertToDriverDTO(driver);
        } else if (user instanceof Requester requester) {
            return convertToRequesterDTO(requester);
        } else {
            return convertToBaseUserDTO(user);
        }
    }
    
    // Base user mapping
    @Mapping(source = "token", target = "token")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "userAccountType", target = "userAccountType")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "walletBalance", target = "walletBalance")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "profilePicturePath", target = "profilePicturePath")
    AuthenticatedUserDTO convertToBaseUserDTO(User user);
    
    // Driver mapping
    @Mapping(source = "token", target = "token")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "userAccountType", target = "userAccountType")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "walletBalance", target = "walletBalance")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "profilePicturePath", target = "profilePicturePath")
    @Mapping(source = "driverLicensePath", target = "driverLicensePath")
    @Mapping(source = "driverInsurancePath", target = "driverInsurancePath")
    @Mapping(source = "preferredRange", target = "preferredRange")
    @Mapping(source = "car", target = "carDTO", qualifiedByName = "mapCar")
    @Mapping(source = "location", target = "location", qualifiedByName = "mapLocation")
    AuthenticatedDriverDTO convertToDriverDTO(Driver driver);
    
    // Requester mapping
    @Mapping(source = "token", target = "token")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "userAccountType", target = "userAccountType")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "walletBalance", target = "walletBalance")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "profilePicturePath", target = "profilePicturePath")
    // Add requester-specific mappings here if needed
    AuthenticatedRequesterDTO convertToRequesterDTO(Requester requester);
    
    // Custom mappers for nested objects
    @Named("mapCar")
    default CarDTO mapCar(ch.uzh.ifi.hase.soprafs24.entity.Car car) {
        if (car == null) {
            return null;
        }
        return CarDTOMapper.INSTANCE.convertEntityToCarDTO(car);
    }
    
    @Named("mapLocation")
    default LocationDTO mapLocation(ch.uzh.ifi.hase.soprafs24.entity.Location location) {
        if (location == null) {
            return null;
        }
        return LocationDTOMapper.INSTANCE.convertEntityToLocationDTO(location);
    }
} 