package ch.uzh.ifi.hase.soprafs24.security.registration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.DriverRegisterDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.dto.UserRegistrationRequestDTO;
import ch.uzh.ifi.hase.soprafs24.security.registration.service.UserRegistrationService;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserDTOMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRegistrationService userRegistrationService;

    @MockBean
    private UserDTOMapper userDTOMapper;

    private User testUser;
    private UserRegistrationRequestDTO testRequestDTO;

    @BeforeEach
    public void setup() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setToken("test-token");

        // Create test request DTO
        testRequestDTO = new UserRegistrationRequestDTO();
        testRequestDTO.setUser(new DriverRegisterDTO());
        testRequestDTO.getUser().setUsername("testuser");
        testRequestDTO.getUser().setPassword("password");
        testRequestDTO.getUser().setUserAccountType(UserAccountType.DRIVER);
    }

    @Test
    public void registerUser_success() throws Exception {
        // given
        when(userRegistrationService.registerUser(any(), any(), any())).thenReturn(testUser);
        when(userDTOMapper.convertToDTO(any())).thenReturn(new AuthenticatedUserDTO());

        // when/then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void registerUser_missingUserData() throws Exception {
        // given
        testRequestDTO.setUser(null);

        // when/then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testRequestDTO)))
                .andExpect(status().isBadRequest());
    }
} 