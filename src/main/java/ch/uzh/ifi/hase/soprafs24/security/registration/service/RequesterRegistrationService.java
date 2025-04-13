package ch.uzh.ifi.hase.soprafs24.security.registration.service;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.RequesterRegisterDTO;

@Service
public class RequesterRegistrationService {
    

    public Requester registerRequester(
        RequesterRegisterDTO requesterRegisterDTO) {
            Requester requester = new Requester();
            return requester;
    }
}
