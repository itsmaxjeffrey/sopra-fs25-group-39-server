package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.AuthRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.AuthResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.security.JwtTokenUtil;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userService.getUserByUsername(loginRequest.getUsername());
        String jwt = jwtTokenUtil.generateToken(user);
        
        return ResponseEntity.ok(new AuthResponseDTO(
                jwt,
                user.getUserId(),
                user.getUsername(),
                user.getUserAccountType().toString()
        ));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerUser(@RequestBody UserPostDTO userPostDTO) {
        User createdUser = userService.createUser(userPostDTO);
        String jwt = jwtTokenUtil.generateToken(createdUser);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(
                        jwt,
                        createdUser.getUserId(),
                        createdUser.getUsername(),
                        createdUser.getUserAccountType().toString()
                ));
    }
}
