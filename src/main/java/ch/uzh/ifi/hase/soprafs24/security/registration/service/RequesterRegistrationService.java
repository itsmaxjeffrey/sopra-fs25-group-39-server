package ch.uzh.ifi.hase.soprafs24.security.registration.service;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.entity.Requester;

@Service
public class RequesterRegistrationService {
    
    public Requester registerRequester() {
        // Set properties from DTO if needed
        return new Requester();
    }
}
