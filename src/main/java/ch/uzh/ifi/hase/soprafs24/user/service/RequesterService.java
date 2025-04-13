package ch.uzh.ifi.hase.soprafs24.user.service;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.RequesterUpdateDTO;

/**
 * Service for requester-specific operations
 */
@Service
public class RequesterService extends AbstractUserService {
    
    public RequesterService(
            UserRepository userRepository,
            AuthorizationService authorizationService) {
        super(userRepository, authorizationService);
    }
    
    /**
     * Updates a requester with the provided update DTO
     */
    public Requester updateRequesterDetails(Requester requester, RequesterUpdateDTO updates) {
        // Update common fields
        updateCommonFields(requester, updates);
        
        // Add requester-specific field updates here if needed in the future
        
        return requester;
    }
}