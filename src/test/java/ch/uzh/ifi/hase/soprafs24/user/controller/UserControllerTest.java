package ch.uzh.ifi.hase.soprafs24.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.authentication.dto.response.AuthenticatedUserDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import ch.uzh.ifi.hase.soprafs24.user.dto.request.update.BaseUserUpdateDTO;
import ch.uzh.ifi.hase.soprafs24.user.dto.response.PublicUserDTO;
import ch.uzh.ifi.hase.soprafs24.user.mapper.PublicUserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.user.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs24.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PublicUserDTOMapper publicUserDTOMapper;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private UserDTOMapper userDTOMapper;

    private User testUser;
    private BaseUserUpdateDTO testUpdateDTO;
    private AuthenticatedUserDTO testAuthDTO;
    private PublicUserDTO testPublicDTO;

    @BeforeEach
    void setup() {
        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setUserAccountType(UserAccountType.DRIVER);

        // Create test DTOs
        testUpdateDTO = new BaseUserUpdateDTO();
        testUpdateDTO.setUsername("updateduser");
        testUpdateDTO.setUserAccountType(UserAccountType.DRIVER);

        testAuthDTO = new AuthenticatedUserDTO();
        testAuthDTO.setUserId(1L);
        testAuthDTO.setUsername("testuser");

        testPublicDTO = new PublicUserDTO();
        testPublicDTO.setUserId(1L);
        testPublicDTO.setUsername("testuser");
    }

    @Test
    void getUserById_ownProfile_success() throws Exception {
        // given
        when(userService.getUserById(1L, "valid-token", 1L)).thenReturn(testUser);
        when(userDTOMapper.convertToDTO(any(User.class))).thenReturn(testAuthDTO);

        // when/then
        mockMvc.perform(get("/api/v1/users/1")
                .header("UserId", "1")
                .header("Authorization", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserById_otherProfile_success() throws Exception {
        // given
        when(userService.getUserById(1L, "valid-token", 2L)).thenReturn(testUser);
        when(publicUserDTOMapper.convertToPublicUserDTO(any(User.class))).thenReturn(testPublicDTO);

        // when/then
        mockMvc.perform(get("/api/v1/users/2")
                .header("UserId", "1")
                .header("Authorization", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserById_unauthorized() throws Exception {
        // given
        when(userService.getUserById(1L, "invalid-token", 1L))
            .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        // when/then
        mockMvc.perform(get("/api/v1/users/1")
                .header("UserId", "1")
                .header("Authorization", "invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void updateUser_success() throws Exception {
        // given
        when(userService.editUser(eq(1L), eq("valid-token"), any(BaseUserUpdateDTO.class))).thenReturn(testUser);
        when(userDTOMapper.convertToDTO(any(User.class))).thenReturn(testAuthDTO);

        // when/then
        mockMvc.perform(put("/api/v1/users/1")
                .header("Authorization", "valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(testUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
} 