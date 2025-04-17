package ch.uzh.ifi.hase.soprafs24.security.registration.service;
import java.util.UUID;

import org.springframework.stereotype.Service;



@Service
public class TokenService {



    //generate token
    public String generateToken() {
        return UUID.randomUUID().toString();
    }


}
