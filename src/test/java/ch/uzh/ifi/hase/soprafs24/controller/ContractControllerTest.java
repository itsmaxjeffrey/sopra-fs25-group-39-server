package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.service.ContractService;
import ch.uzh.ifi.hase.soprafs24.service.LocationService;
import ch.uzh.ifi.hase.soprafs24.service.ContractPollingService;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractCancelDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractPutDTO;

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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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

    @Test
    public void getAllContracts_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        List<Contract> allContracts = Collections.singletonList(contract);
        given(contractService.getContracts(null, null, null)).willReturn(allContracts);

        // when/then
        mockMvc.perform(get("/api/v1/contracts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contractId", is(contract.getContractId().intValue())))
                .andExpect(jsonPath("$[0].title", is(contract.getTitle())))
                .andExpect(jsonPath("$[0].contractStatus", is("REQUESTED")));
    }

    @Test
    public void getContractById_success() throws Exception {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setTitle("Test Contract");
        contract.setContractStatus(ContractStatus.REQUESTED);

        given(contractService.getContractById(1L)).willReturn(contract);

        // when/then
        mockMvc.perform(get("/api/v1/contracts/1")
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
    public void deleteContract_success() throws Exception {
        // given
        Mockito.doNothing().when(contractService).deleteContract(1L);

        // when/then
        mockMvc.perform(delete("/api/v1/contracts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
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