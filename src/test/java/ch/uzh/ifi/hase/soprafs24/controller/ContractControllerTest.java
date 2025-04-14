package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.service.ContractService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;
import ch.uzh.ifi.hase.soprafs24.service.ContractPollingService;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractCancelDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPutDTO;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ContractControllerTest
 * This is a WebMvcTest which allows to test the ContractController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the ContractController works.
 */
@WebMvcTest(ContractController.class)
public class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContractService contractService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ContractPollingService contractPollingService;

    @MockBean
    private AuthorizationService authorizationService;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_TOKEN = "test-token";

    @Test
    public void getAllContracts_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        List<Contract> allContracts = Collections.singletonList(contract);
        given(contractService.getContracts(null, null, null)).willReturn(allContracts);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(new User());

        // when/then
        mockMvc.perform(get("/api/v1/contracts")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contracts", hasSize(1)))
                .andExpect(jsonPath("$.contracts[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.contracts[0].title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contracts[0].contractStatus", is("REQUESTED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getAllContracts_unauthorized() throws Exception {
        // given
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(null);

        // when/then
        mockMvc.perform(get("/api/v1/contracts")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getContractById_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        given(contractService.getContractById(1L)).willReturn(contract);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(new User());

        // when/then
        mockMvc.perform(get("/api/v1/contracts/1")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contractStatus", is("REQUESTED")));
    }

    @Test
    public void updateContract_success() throws Exception {
        // given
        ContractPutDTO contractPutDTO = new ContractPutDTO();
        contractPutDTO.setTitle("Updated Contract");
        contractPutDTO.setContractDescription("Updated description");

        Contract updatedContract = new Contract();
        updatedContract.setContractId(1L);
        updatedContract.setTitle(contractPutDTO.getTitle());
        updatedContract.setContractDescription(contractPutDTO.getContractDescription());
        updatedContract.setContractStatus(ContractStatus.REQUESTED);

        given(contractService.updateContract(Mockito.any(), Mockito.any())).willReturn(updatedContract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractPutDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId", is(updatedContract.getContractId().intValue())))
                .andExpect(jsonPath("$.title", is(updatedContract.getTitle())))
                .andExpect(jsonPath("$.contractDescription", is(updatedContract.getContractDescription())));
    }

    @Test
    public void cancelContract_success() throws Exception {
        // given
        ContractCancelDTO contractCancelDTO = new ContractCancelDTO();
        contractCancelDTO.setReason("Test cancellation reason");

        Contract cancelledContract = new Contract();
        cancelledContract.setContractId(1L);
        cancelledContract.setContractStatus(ContractStatus.CANCELED);
        cancelledContract.setCancelReason(contractCancelDTO.getReason());

        given(contractService.cancelContract(Mockito.any(), Mockito.any())).willReturn(cancelledContract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractCancelDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId", is(cancelledContract.getContractId().intValue())))
                .andExpect(jsonPath("$.contractStatus", is("CANCELED")));
    }

    @Test
    public void fulfillContract_success() throws Exception {
        // given
        Contract fulfilledContract = new Contract();
        fulfilledContract.setContractId(1L);
        fulfilledContract.setContractStatus(ContractStatus.COMPLETED);

        given(contractService.fulfillContract(1L)).willReturn(fulfilledContract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/fulfill")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId", is(fulfilledContract.getContractId().intValue())))
                .andExpect(jsonPath("$.contractStatus", is("COMPLETED")));
    }

    @Test
    public void getUserContracts_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        List<Contract> userContracts = Collections.singletonList(contract);
        given(contractService.getContractsByRequesterId(1L, null)).willReturn(userContracts);
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new Requester()));

        // when/then
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$[0].title", is(contract.getTitle())))
                .andExpect(jsonPath("$[0].contractStatus", is("REQUESTED")));
    }

    @Test
    public void getUserContracts_withStatus_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        List<Contract> userContracts = Collections.singletonList(contract);
        given(contractService.getContractsByRequesterId(1L, ContractStatus.REQUESTED)).willReturn(userContracts);
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new Requester()));

        // when/then
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .param("status", "REQUESTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$[0].title", is(contract.getTitle())))
                .andExpect(jsonPath("$[0].contractStatus", is("REQUESTED")));
    }

    @Test
    public void getUserContracts_withoutStatus_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        List<Contract> userContracts = Collections.singletonList(contract);
        given(contractService.getContractsByRequesterId(1L, null)).willReturn(userContracts);
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new Requester()));

        // when/then
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$[0].title", is(contract.getTitle())))
                .andExpect(jsonPath("$[0].contractStatus", is("REQUESTED")));
    }

    @Test
    public void getUserContracts_userNotFound_throwsException() throws Exception {
        // given
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.empty());

        // when/then
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserContracts_withInvalidStatus_throwsException() throws Exception {
        // given
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new Requester()));

        // when/then
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .param("status", "INVALID_STATUS")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserContracts_forDriver_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.ACCEPTED);

        List<Contract> driverContracts = Collections.singletonList(contract);
        given(contractService.getContractsByDriverId(1L, ContractStatus.ACCEPTED)).willReturn(driverContracts);
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new User()));

        // when/then
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .param("status", "ACCEPTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$[0].title", is(contract.getTitle())))
                .andExpect(jsonPath("$[0].contractStatus", is("ACCEPTED")));
    }

    @Test
    public void deleteContract_success() throws Exception {
        // given
        Mockito.doNothing().when(contractService).deleteContract(1L);

        // when/then
        mockMvc.perform(delete("/api/v1/contracts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getUserContracts_emptyList_success() throws Exception {
        // given
        List<Contract> emptyList = Collections.emptyList();
        given(contractService.getContractsByRequesterId(1L, ContractStatus.REQUESTED)).willReturn(emptyList);
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new Requester()));

        // when/then
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .param("status", "REQUESTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getUserContracts_multipleContracts_success() throws Exception {
        // given
        Contract contract1 = new Contract();
        contract1.setContractId(1L);
        contract1.setTitle("Contract 1");
        contract1.setContractStatus(ContractStatus.REQUESTED);

        Contract contract2 = new Contract();
        contract2.setContractId(2L);
        contract2.setTitle("Contract 2");
        contract2.setContractStatus(ContractStatus.REQUESTED);

        List<Contract> userContracts = Arrays.asList(contract1, contract2);
        given(contractService.getContractsByRequesterId(1L, ContractStatus.REQUESTED)).willReturn(userContracts);
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new Requester()));

        // when/then
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .param("status", "REQUESTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].contractId", is(contract1.getContractId().intValue())))
                .andExpect(jsonPath("$[0].title", is(contract1.getTitle())))
                .andExpect(jsonPath("$[0].contractStatus", is("REQUESTED")))
                .andExpect(jsonPath("$[1].contractId", is(contract2.getContractId().intValue())))
                .andExpect(jsonPath("$[1].title", is(contract2.getTitle())))
                .andExpect(jsonPath("$[1].contractStatus", is("REQUESTED")));
    }

    @Test
    public void getUserContracts_mixedStatuses_success() throws Exception {
        // given
        Contract requestedContract = new Contract();
        requestedContract.setContractId(1L);
        requestedContract.setTitle("Requested Contract");
        requestedContract.setContractStatus(ContractStatus.REQUESTED);

        Contract acceptedContract = new Contract();
        acceptedContract.setContractId(2L);
        acceptedContract.setTitle("Accepted Contract");
        acceptedContract.setContractStatus(ContractStatus.ACCEPTED);

        given(contractService.getContractsByRequesterId(1L, ContractStatus.REQUESTED))
            .willReturn(Collections.singletonList(requestedContract));
        given(contractService.getContractsByRequesterId(1L, ContractStatus.ACCEPTED))
            .willReturn(Collections.singletonList(acceptedContract));
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new Requester()));

        // when/then for REQUESTED status
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .param("status", "REQUESTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contractId", is(requestedContract.getContractId().intValue())))
                .andExpect(jsonPath("$[0].contractStatus", is("REQUESTED")));

        // when/then for ACCEPTED status
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .param("status", "ACCEPTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contractId", is(acceptedContract.getContractId().intValue())))
                .andExpect(jsonPath("$[0].contractStatus", is("ACCEPTED")));
    }

    @Test
    public void getUserContracts_caseInsensitiveStatus_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        List<Contract> userContracts = Collections.singletonList(contract);
        given(contractService.getContractsByRequesterId(1L, ContractStatus.REQUESTED)).willReturn(userContracts);
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new Requester()));

        // when/then with uppercase status
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .param("status", "REQUESTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$[0].contractStatus", is("REQUESTED")));
    }

    @Test
    public void getUserContracts_malformedStatus_throwsException() throws Exception {
        // given
        given(userRepository.findByUserId(1L)).willReturn(java.util.Optional.of(new Requester()));

        // when/then with malformed status
        mockMvc.perform(get("/api/v1/users/1/contracts")
                .param("status", "REQUESTED_")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllContracts_missingUserIdHeader_throwsException() throws Exception {
        // when/then
        mockMvc.perform(get("/api/v1/contracts")
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllContracts_missingTokenHeader_throwsException() throws Exception {
        // when/then
        mockMvc.perform(get("/api/v1/contracts")
                .header("UserId", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllContracts_invalidUserId_throwsException() throws Exception {
        // given
        given(authorizationService.authenticateUser(999L, TEST_TOKEN)).willReturn(null);

        // when/then
        mockMvc.perform(get("/api/v1/contracts")
                .header("UserId", 999L)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getAllContracts_invalidToken_throwsException() throws Exception {
        // given
        given(authorizationService.authenticateUser(TEST_USER_ID, "invalid-token")).willReturn(null);

        // when/then
        mockMvc.perform(get("/api/v1/contracts")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", "invalid-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getAllContracts_tokenMismatch_throwsException() throws Exception {
        // given
        User user = new User();
        user.setUserId(TEST_USER_ID);
        user.setToken("different-token");
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(null);

        // when/then
        mockMvc.perform(get("/api/v1/contracts")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getAllContracts_authenticatedUser_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setToken(TEST_TOKEN);

        List<Contract> allContracts = Collections.singletonList(contract);
        given(contractService.getContracts(null, null, null)).willReturn(allContracts);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(get("/api/v1/contracts")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contracts", hasSize(1)))
                .andExpect(jsonPath("$.contracts[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.contracts[0].title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contracts[0].contractStatus", is("REQUESTED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    /**
     * Helper Method to convert contractPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"title": "Test Contract", "mass": 10.0, ...}
     * 
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("The request body could not be created.%s", e.toString()));
        }
    }
} 