package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.service.ContractService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;
import ch.uzh.ifi.hase.soprafs24.service.ContractPollingService;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractCancelDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPostDTO;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
        
        // Set up the requester
        Requester requester = new Requester();
        requester.setUserId(TEST_USER_ID);
        contract.setRequester(requester);

        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        given(contractService.getContractById(1L)).willReturn(contract);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(get("/api/v1/contracts/1")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.contract.title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contract.contractStatus", is("REQUESTED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getContractById_requesterAccessOwnContract_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);
        
        // Set up the requester
        Requester requester = new Requester();
        requester.setUserId(TEST_USER_ID);
        contract.setRequester(requester);

        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        given(contractService.getContractById(1L)).willReturn(contract);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(get("/api/v1/contracts/1")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.contract.title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contract.contractStatus", is("REQUESTED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getContractById_requesterAccessOtherContract_throwsException() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);
        
        // Set up a different requester
        Requester requester = new Requester();
        requester.setUserId(999L); // Different user ID
        contract.setRequester(requester);

        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        given(contractService.getContractById(1L)).willReturn(contract);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(get("/api/v1/contracts/1")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("You are not authorized to view this contract")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getContractById_driverAccessUnassignedContract_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);
        
        // Contract has no driver assigned
        contract.setDriver(null);

        // Set up authenticated user as driver
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.DRIVER);

        given(contractService.getContractById(1L)).willReturn(contract);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(get("/api/v1/contracts/1")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.contract.title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contract.contractStatus", is("REQUESTED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getContractById_driverAccessAssignedContract_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.ACCEPTED);
        
        // Set up the driver
        Driver driver = new Driver();
        driver.setUserId(TEST_USER_ID);
        contract.setDriver(driver);

        // Set up authenticated user as driver
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.DRIVER);

        given(contractService.getContractById(1L)).willReturn(contract);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(get("/api/v1/contracts/1")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.contract.title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contract.contractStatus", is("ACCEPTED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getContractById_driverAccessOtherDriverContract_throwsException() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.ACCEPTED);
        
        // Set up a different driver
        Driver driver = new Driver();
        driver.setUserId(999L); // Different user ID
        contract.setDriver(driver);

        // Set up authenticated user as driver
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.DRIVER);

        given(contractService.getContractById(1L)).willReturn(contract);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(get("/api/v1/contracts/1")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("You are not authorized to view this contract")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getContractById_driverAccessOfferedContract_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.OFFERED);
        
        // Contract has no driver assigned yet
        contract.setDriver(null);

        // Set up authenticated user as driver
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.DRIVER);

        given(contractService.getContractById(1L)).willReturn(contract);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(get("/api/v1/contracts/1")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.contract.title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contract.contractStatus", is("OFFERED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void updateContract_success() throws Exception {
        // given
        ContractPutDTO contractPutDTO = new ContractPutDTO();
        contractPutDTO.setTitle("Updated Contract");
        contractPutDTO.setContractDescription("Updated description");

        // Set up authenticated user
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        // Set up existing contract
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        Requester requester = new Requester();
        requester.setUserId(TEST_USER_ID);
        existingContract.setRequester(requester);

        // Set up updated contract
        Contract updatedContract = new Contract();
        updatedContract.setContractId(1L);
        updatedContract.setTitle(contractPutDTO.getTitle());
        updatedContract.setContractDescription(contractPutDTO.getContractDescription());
        updatedContract.setContractStatus(ContractStatus.REQUESTED);
        updatedContract.setRequester(requester);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(contractService.getContractById(1L)).willReturn(existingContract);
        given(contractService.updateContract(Mockito.any(), Mockito.any())).willReturn(updatedContract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractPutDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.contractId", is(updatedContract.getContractId().intValue())))
                .andExpect(jsonPath("$.contract.title", is(updatedContract.getTitle())))
                .andExpect(jsonPath("$.contract.contractDescription", is(updatedContract.getContractDescription())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void cancelContract_success() throws Exception {
        // given
        ContractCancelDTO contractCancelDTO = new ContractCancelDTO();
        contractCancelDTO.setReason("Test cancellation reason");

        // Set up the contract
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setContractStatus(ContractStatus.ACCEPTED);
        contract.setMoveDateTime(LocalDateTime.now().plusDays(5)); // Set move date more than 72 hours in future
        
        // Set up the requester
        Requester requester = new Requester();
        requester.setUserId(TEST_USER_ID);
        contract.setRequester(requester);

        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        // Set up cancelled contract
        Contract cancelledContract = new Contract();
        cancelledContract.setContractId(1L);
        cancelledContract.setContractStatus(ContractStatus.CANCELED);
        cancelledContract.setCancelReason(contractCancelDTO.getReason());
        cancelledContract.setRequester(requester);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(contractService.getContractById(1L)).willReturn(contract);
        given(contractService.cancelContract(Mockito.any(), Mockito.any())).willReturn(cancelledContract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/cancel")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractCancelDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.contractId", is(cancelledContract.getContractId().intValue())))
                .andExpect(jsonPath("$.contract.contractStatus", is("CANCELED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void cancelContract_badRequest_notAccepted() throws Exception {
        // given
        ContractCancelDTO contractCancelDTO = new ContractCancelDTO();
        contractCancelDTO.setReason("Test cancellation reason");

        // Set up the contract in REQUESTED state
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setContractStatus(ContractStatus.REQUESTED);
        
        // Set up the requester
        Requester requester = new Requester();
        requester.setUserId(TEST_USER_ID);
        contract.setRequester(requester);

        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(contractService.getContractById(1L)).willReturn(contract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/cancel")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractCancelDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Only ACCEPTED contracts can be cancelled. Use delete for REQUESTED or OFFERED contracts.")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void cancelContract_unauthorized() throws Exception {
        // given
        ContractCancelDTO contractCancelDTO = new ContractCancelDTO();
        contractCancelDTO.setReason("Test cancellation reason");

        // Mock authentication failure
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(null);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/cancel")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractCancelDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void cancelContract_forbidden_driver() throws Exception {
        // given
        ContractCancelDTO contractCancelDTO = new ContractCancelDTO();
        contractCancelDTO.setReason("Test cancellation reason");

        // Set up authenticated user as driver
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.DRIVER);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/cancel")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractCancelDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Only requesters can cancel contracts")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void cancelContract_forbidden_otherRequester() throws Exception {
        // given
        ContractCancelDTO contractCancelDTO = new ContractCancelDTO();
        contractCancelDTO.setReason("Test cancellation reason");

        // Set up the contract with a different requester
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setContractStatus(ContractStatus.REQUESTED);
        
        Requester requester = new Requester();
        requester.setUserId(999L); // Different user ID
        contract.setRequester(requester);

        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(contractService.getContractById(1L)).willReturn(contract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/cancel")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractCancelDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("You are not authorized to cancel this contract")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void cancelContract_badRequest_missingReason() throws Exception {
        // given
        ContractCancelDTO contractCancelDTO = new ContractCancelDTO();
        contractCancelDTO.setReason(""); // Empty reason

        // Set up the contract in ACCEPTED state
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setContractStatus(ContractStatus.ACCEPTED);
        contract.setMoveDateTime(LocalDateTime.now().plusDays(5)); // Set move date more than 72 hours in future
        
        // Set up the requester
        Requester requester = new Requester();
        requester.setUserId(TEST_USER_ID);
        contract.setRequester(requester);

        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(contractService.getContractById(1L)).willReturn(contract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/cancel")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractCancelDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Cancellation reason is required")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void fulfillContract_success_requester() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setContractStatus(ContractStatus.COMPLETED);
        
        // Set up the requester
        Requester requester = new Requester();
        requester.setUserId(TEST_USER_ID);
        contract.setRequester(requester);

        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        // Set up fulfilled contract
        Contract fulfilledContract = new Contract();
        fulfilledContract.setContractId(1L);
        fulfilledContract.setContractStatus(ContractStatus.FINALIZED);
        fulfilledContract.setRequester(requester);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(contractService.getContractById(1L)).willReturn(contract);
        given(contractService.fulfillContract(1L)).willReturn(fulfilledContract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/fulfill")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.contractId", is(fulfilledContract.getContractId().intValue())))
                .andExpect(jsonPath("$.contract.contractStatus", is("FINALIZED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void fulfillContract_success_driver() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setContractStatus(ContractStatus.COMPLETED);
        
        // Set up the driver
        Driver driver = new Driver();
        driver.setUserId(TEST_USER_ID);
        contract.setDriver(driver);

        // Set up authenticated user as driver
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.DRIVER);

        // Set up fulfilled contract
        Contract fulfilledContract = new Contract();
        fulfilledContract.setContractId(1L);
        fulfilledContract.setContractStatus(ContractStatus.FINALIZED);
        fulfilledContract.setDriver(driver);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(contractService.getContractById(1L)).willReturn(contract);
        given(contractService.fulfillContract(1L)).willReturn(fulfilledContract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/fulfill")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.contractId", is(fulfilledContract.getContractId().intValue())))
                .andExpect(jsonPath("$.contract.contractStatus", is("FINALIZED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void fulfillContract_unauthorized() throws Exception {
        // given
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(null);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/fulfill")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void fulfillContract_forbidden_otherRequester() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        
        // Set up a different requester
        Requester requester = new Requester();
        requester.setUserId(999L); // Different user ID
        contract.setRequester(requester);

        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(contractService.getContractById(1L)).willReturn(contract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/fulfill")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("You are not authorized to fulfill this contract")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void fulfillContract_forbidden_otherDriver() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        
        // Set up a different driver
        Driver driver = new Driver();
        driver.setUserId(999L); // Different user ID
        contract.setDriver(driver);

        // Set up authenticated user as driver
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.DRIVER);

        // Mock service responses
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(contractService.getContractById(1L)).willReturn(contract);

        // when/then
        mockMvc.perform(put("/api/v1/contracts/1/fulfill")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("You are not authorized to fulfill this contract")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getUserContracts_success_requester() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        List<Contract> contracts = Collections.singletonList(contract);
        given(contractService.getContractsByRequesterId(TEST_USER_ID, ContractStatus.REQUESTED)).willReturn(contracts);
        
        // Set up authenticated user as requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        
        // Set up user repository response
        given(userRepository.findByUserId(TEST_USER_ID)).willReturn(java.util.Optional.of(new Requester()));

        // when/then
        mockMvc.perform(get("/api/v1/users/" + TEST_USER_ID + "/contracts")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .param("status", "REQUESTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contracts", hasSize(1)))
                .andExpect(jsonPath("$.contracts[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.contracts[0].title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contracts[0].contractStatus", is("REQUESTED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getUserContracts_success_driver() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.ACCEPTED);

        List<Contract> contracts = Collections.singletonList(contract);
        given(contractService.getContractsByDriverId(TEST_USER_ID, ContractStatus.ACCEPTED)).willReturn(contracts);
        
        // Set up authenticated user as driver
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.DRIVER);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        
        // Set up user repository response
        given(userRepository.findByUserId(TEST_USER_ID)).willReturn(java.util.Optional.of(new Driver()));

        // when/then
        mockMvc.perform(get("/api/v1/users/" + TEST_USER_ID + "/contracts")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .param("status", "ACCEPTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contracts", hasSize(1)))
                .andExpect(jsonPath("$.contracts[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$.contracts[0].title", is(contract.getTitle())))
                .andExpect(jsonPath("$.contracts[0].contractStatus", is("ACCEPTED")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getUserContracts_unauthorized() throws Exception {
        // given
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(null);

        // when/then
        mockMvc.perform(get("/api/v1/users/" + TEST_USER_ID + "/contracts")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getUserContracts_forbidden_otherUser() throws Exception {
        // given
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // when/then
        mockMvc.perform(get("/api/v1/users/999/contracts") // Different user ID
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("You are not authorized to view these contracts")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void getUserContracts_userNotFound() throws Exception {
        // given
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);
        given(userRepository.findByUserId(TEST_USER_ID)).willReturn(java.util.Optional.empty());

        // when/then
        mockMvc.perform(get("/api/v1/users/" + TEST_USER_ID + "/contracts")
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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

    @Test
    public void createContract_unauthorized() throws Exception {
        // Setup test data
        ContractPostDTO contractPostDTO = new ContractPostDTO();
        contractPostDTO.setTitle("Test Contract");

        // Mock authentication failure
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(null);

        // Perform request
        mockMvc.perform(post("/api/v1/contracts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .content(asJsonString(contractPostDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void createContract_forbidden() throws Exception {
        // Setup test data
        ContractPostDTO contractPostDTO = new ContractPostDTO();
        contractPostDTO.setTitle("Test Contract");

        // Mock authentication for non-requester
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.DRIVER);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // Perform request
        mockMvc.perform(post("/api/v1/contracts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .content(asJsonString(contractPostDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Only requesters can create contracts")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    public void createContract_missingHeaders() throws Exception {
        // Setup test data
        ContractPostDTO contractPostDTO = new ContractPostDTO();
        contractPostDTO.setTitle("Test Contract");

        // Perform request without headers
        mockMvc.perform(post("/api/v1/contracts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(contractPostDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createContract_invalidData() throws Exception {
        // Setup test data with invalid values
        ContractPostDTO contractPostDTO = new ContractPostDTO();
        contractPostDTO.setTitle(""); // Empty title
        contractPostDTO.setMass(-1.0f); // Negative mass
        contractPostDTO.setVolume(-1.0f); // Negative volume
        contractPostDTO.setManPower(-1); // Negative man power
        contractPostDTO.setPrice(-1.0f); // Negative price
        contractPostDTO.setCollateral(-1.0f); // Negative collateral
        contractPostDTO.setMoveDateTime(LocalDateTime.now().minusDays(1)); // Past date

        // Mock authentication
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUserAccountType(UserAccountType.REQUESTER);
        given(authorizationService.authenticateUser(TEST_USER_ID, TEST_TOKEN)).willReturn(authenticatedUser);

        // Perform request
        mockMvc.perform(post("/api/v1/contracts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("UserId", TEST_USER_ID)
                .header("Authorization", TEST_TOKEN)
                .content(asJsonString(contractPostDTO)))
                .andExpect(status().isBadRequest());
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