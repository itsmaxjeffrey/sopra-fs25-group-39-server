package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedDriverDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedRequesterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedUserDTO;

@Service
public class UserDTOMapper {
    
    // Use the existing mappers
    private final CarDTOMapper carDTOMapper = CarDTOMapper.INSTANCE;
    private final LocationDTOMapper locationDTOMapper = LocationDTOMapper.INSTANCE;
    
    public AuthenticatedUserDTO convertToDTO(User user) {
        if (user instanceof Driver) {
            return convertToDriverDTO((Driver) user);
        } else if (user instanceof Requester) {
            return convertToRequesterDTO((Requester) user);
        } else {
            return convertToBaseUserDTO(user);
        }
    }
    
    private AuthenticatedUserDTO convertToBaseUserDTO(User user) {
        AuthenticatedUserDTO dto = new AuthenticatedUserDTO();
        // Map common fields
        dto.setToken(user.getToken());
        dto.setUserId(user.getUserId());
        dto.setUserAccountType(user.getUserAccountType());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setWalletBalance(user.getWalletBalance());
        dto.setBirthDate(user.getBirthDate());
        dto.setUserBio(user.getUserBio());
        dto.setProfilePicturePath(user.getProfilePicturePath());
        return dto;
    }
    
    private AuthenticatedDriverDTO convertToDriverDTO(Driver driver) {
        AuthenticatedDriverDTO dto = new AuthenticatedDriverDTO();
        // Map base fields
        dto.setToken(driver.getToken());
        dto.setUserId(driver.getUserId());
        dto.setUserAccountType(driver.getUserAccountType());
        dto.setUsername(driver.getUsername());
        dto.setEmail(driver.getEmail());
        dto.setFirstName(driver.getFirstName());
        dto.setLastName(driver.getLastName());
        dto.setPhoneNumber(driver.getPhoneNumber());
        dto.setWalletBalance(driver.getWalletBalance());
        dto.setBirthDate(driver.getBirthDate());
        dto.setUserBio(driver.getUserBio());
        dto.setProfilePicturePath(driver.getProfilePicturePath());
        
        // Map driver-specific fields
        dto.setDriverLicensePath(driver.getDriverLicensePath());
        dto.setDriverInsurancePath(driver.getDriverInsurancePath());
        dto.setPreferredRange(driver.getPreferredRange());
        
        // Use CarDTOMapper instead of manual mapping
        if (driver.getCar() != null) {
            dto.setCar(carDTOMapper.convertEntityToCarDTO(driver.getCar()));
        }
        
        // Use LocationDTOMapper instead of manual mapping
        if (driver.getLocation() != null) {
            dto.setLocation(locationDTOMapper.convertEntityToLocationDTO(driver.getLocation()));
        }
        
        return dto;
    }
    
    private AuthenticatedRequesterDTO convertToRequesterDTO(Requester requester) {
        AuthenticatedRequesterDTO dto = new AuthenticatedRequesterDTO();
        // Map base fields
        dto.setToken(requester.getToken());
        dto.setUserId(requester.getUserId());
        dto.setUserAccountType(requester.getUserAccountType());
        dto.setUsername(requester.getUsername());
        dto.setEmail(requester.getEmail());
        dto.setFirstName(requester.getFirstName());
        dto.setLastName(requester.getLastName());
        dto.setPhoneNumber(requester.getPhoneNumber());
        dto.setWalletBalance(requester.getWalletBalance());
        dto.setBirthDate(requester.getBirthDate());
        dto.setUserBio(requester.getUserBio());
        dto.setProfilePicturePath(requester.getProfilePicturePath());
        
        // Add requester-specific mappings if needed
        
        return dto;
    }
}