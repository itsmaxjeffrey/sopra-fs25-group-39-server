package ch.uzh.ifi.hase.soprafs24.security.authentication.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.request.BaseUserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.security.authentication.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.security.registration.service.UserRegistrationService;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserDTOMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRegistrationService userRegistrationService;

    @MockBean
    private UserDTOMapper userDTOMapper;

    private User testUser;
    private BaseUserLoginDTO testLoginDTO;

    @BeforeEach
    void setup() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setToken("test-token");

        // Create test login DTO
        testLoginDTO = new BaseUserLoginDTO();
        testLoginDTO.setUsername("testuser");
        testLoginDTO.setPassword("password");
    }

    @Test
    void loginUser_success() throws Exception {
        // given
        when(authService.loginUser(any())).thenReturn(testUser);
        when(userDTOMapper.convertToDTO(any())).thenReturn(new AuthenticatedUserDTO());

        // when/then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testLoginDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void logoutUser_success() throws Exception {
        // given
        Mockito.doNothing().when(authService).logoutUser(any(), any());

        // when/then
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("UserId", "1")
                .header("Authorization", "test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully logged out"));
    }

    @Test
    void logoutUser_missingHeaders_unauthorized() throws Exception {
        // when/then
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isBadRequest());
    }
} 