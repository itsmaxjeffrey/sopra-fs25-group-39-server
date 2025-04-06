package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LocationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedDriverDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedRequesterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedUserDTO;

@Service
public class UserDTOMapper {
    
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
        
        // Map car if exists
        if (driver.getCar() != null) {
            CarDTO carDTO = new CarDTO();
            carDTO.setCarModel(driver.getCar().getCarModel());
            carDTO.setSpace(driver.getCar().getSpace());
            carDTO.setSupportedWeight(driver.getCar().getSupportedWeight());
            carDTO.setElectric(driver.getCar().isElectric());
            carDTO.setLicensePlate(driver.getCar().getLicensePlate());
            dto.setCarDTO(carDTO);
        }
        
        // Map location if exists
        if (driver.getLocation() != null) {
            LocationDTO locationDTO = new LocationDTO();
            locationDTO.setLatitude(driver.getLocation().getLatitude());
            locationDTO.setLongitude(driver.getLocation().getLongitude());
            locationDTO.setFormattedAddress(driver.getLocation().getFormattedAddress());
            dto.setLocation(locationDTO);
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