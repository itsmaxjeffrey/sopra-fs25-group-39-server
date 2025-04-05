package ch.uzh.ifi.hase.soprafs24.security;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.register.RequesterRegisterDTO;

@Service
public class RequesterRegisterationService {
    

    public Requester registerRequester(
        RequesterRegisterDTO requesterRegisterDTO) {
            Requester requester = new Requester();
            return requester;
    }
}
