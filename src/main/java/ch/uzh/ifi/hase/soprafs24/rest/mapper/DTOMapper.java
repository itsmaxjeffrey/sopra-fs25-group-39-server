package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractGetDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);
  
  // Car mappings
  @Mapping(source = "carModel", target = "carModel")
  @Mapping(source = "space", target = "space")
  @Mapping(source = "supportedWeight", target = "supportedWeight")
  @Mapping(source = "isElectric", target = "isElectric")
  @Mapping(source = "licensePlate", target = "licensePlate")
  @Mapping(target = "carId", ignore = true)
  @Mapping(target = "carPicturePath", ignore = true)
  @Mapping(target = "driver", ignore = true)
  Car convertCarDTOtoEntity(CarDTO carDTO);

  // Location mappings
  @Mapping(source = "formattedAddress", target = "formattedAddress")
  @Mapping(source = "latitude", target = "latitude")
  @Mapping(source = "longitude", target = "longitude")
  @Mapping(target = "id", ignore = true)
  Location convertLocationDTOtoEntity(LocationDTO locationDTO);
  
  // Actual mapping methods for user type "Requester"
  @Mapping(source = "username", target = "username")
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "userAccountType", target = "userAccountType")
  @Mapping(source = "birthDate", target = "birthDate")
  @Mapping(source = "profilePicturePath", target = "profilePicturePath")
  @Mapping(source = "phoneNumber", target = "phoneNumber")
  @Mapping(source = "userBio", target = "userBio")

  // Ignore the following fields when creating a new user of type "Requester" (either auto-generated or empty)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "walletBalance", ignore = true)
  @Mapping(target = "ratingsGiven", ignore = true)
  @Mapping(target = "ratingsReceived", ignore = true)
  @Mapping(target = "contracts", ignore = true)
  Requester convertUserPostDTOtoRequesterEntity(UserPostDTO userPostDTO);

  // Actual mapping methods for user type "Driver"
  @Mapping(source = "username", target = "username")
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "userAccountType", target = "userAccountType")
  @Mapping(source = "birthDate", target = "birthDate")
  @Mapping(source = "profilePicturePath", target = "profilePicturePath")
  @Mapping(source = "phoneNumber", target = "phoneNumber")
  @Mapping(source = "userBio", target = "userBio")

  // Ignore the following fields when creating a new user of type "Driver" (either auto-generated or empty)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "walletBalance", ignore = true)
  @Mapping(target = "ratingsGiven", ignore = true)
  @Mapping(target = "ratingsReceived", ignore = true)
  

  // Specific to Driver (Car Details)
  @Mapping(source = "car", target = "car")
  @Mapping(target = "driverLicensePath", ignore = true)
  @Mapping(target = "driverInsurancePath", ignore = true)
  @Mapping(target = "location", ignore = true)
  @Mapping(target = "preferredRange", ignore = true)

  Driver convertUserPostDTOtoDriverEntity(UserPostDTO userPostDTO);

  // Actual mapping methods for user type "Driver" to UserGetDTO
  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "userAccountType", target = "userAccountType")
  @Mapping(source = "car", target = "car")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "profilePicturePath", target = "profilePicturePath")
  @Mapping(source = "phoneNumber", target = "phoneNumber")
  @Mapping(source = "userBio", target = "userBio")
  @Mapping(source = "birthDate", target = "birthDate")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "walletBalance", target = "walletBalance")
  @Mapping(source = "ratingsGiven", target = "ratingsGiven")
  @Mapping(source = "ratingsReceived", target = "ratingsReceived")

  // Ignore the following fields when getting a new user of type "Driver"
  @Mapping(target = "contracts", ignore = true)
  @Mapping(target = "password", ignore = true)

  UserGetDTO convertDriverEntityToUserGetDTO(Driver driver);


  // Actual mapping methods for user type "Requester" to UserGetDTO
  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "userAccountType", target = "userAccountType")
  @Mapping(source = "profilePicturePath", target = "profilePicturePath")
  @Mapping(source = "phoneNumber", target = "phoneNumber")
  @Mapping(source = "userBio", target = "userBio")
  @Mapping(source = "birthDate", target = "birthDate")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "walletBalance", target = "walletBalance")
  @Mapping(source = "ratingsGiven", target = "ratingsGiven")
  @Mapping(source = "ratingsReceived", target = "ratingsReceived")

  // Ignore the following fields when getting a new user of type "Requester"
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "contracts", ignore = true)
  @Mapping(target = "car", ignore = true)
  UserGetDTO convertRequesterEntityToUserGetDTO(User user);



    // Contract mappings
    @Mapping(source = "title", target = "title")
    @Mapping(source = "mass", target = "mass")
    @Mapping(source = "volume", target = "volume")
    @Mapping(source = "isFragile", target = "isFragile")
    @Mapping(source = "coolingRequired", target = "coolingRequired")
    @Mapping(source = "rideAlong", target = "rideAlong")
    @Mapping(source = "manPower", target = "manPower")
    @Mapping(source = "contractDescription", target = "contractDescription")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "collateral", target = "collateral")
    @Mapping(source = "moveDateTime", target = "moveDateTime")
    @Mapping(target = "contractId", ignore = true)
    @Mapping(target = "contractStatus", ignore = true)
    @Mapping(target = "creationDateTime", ignore = true)
    @Mapping(target = "acceptedDateTime", ignore = true)
    @Mapping(target = "contractPhotos", ignore = true)
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "acceptedOffer", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "fromAddress", ignore = true)
    @Mapping(target = "toAddress", ignore = true)
    Contract convertContractPostDTOtoEntity(ContractPostDTO contractPostDTO);

    @Mapping(source = "contractId", target = "contractId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "mass", target = "mass")
    @Mapping(source = "volume", target = "volume")
    @Mapping(source = "isFragile", target = "isFragile")
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
    ContractGetDTO convertContractEntityToContractGetDTO(Contract contract);
}
