package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.rest.dto.auth.response.AuthenticatedDriverDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.OfferService;
import ch.uzh.ifi.hase.soprafs24.service.ContractService;
import ch.uzh.ifi.hase.soprafs24.security.authorization.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OfferControllerTest {

    @Mock
    private OfferService offerService;

    @Mock
    private ContractService contractService;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private OfferController offerController;

    private OfferGetDTO testOfferGetDTO;
    private OfferPostDTO testOfferPostDTO;
    private OfferPutDTO testOfferPutDTO;
    private User testDriver;
    private User testRequester;
    private ContractGetDTO testContractGetDTO;
    private AuthenticatedDriverDTO testDriverDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test driver
        testDriver = new User();
        testDriver.setUserId(2L);
        testDriver.setUserAccountType(UserAccountType.DRIVER);
        testDriver.setToken("test-token");

        // Create test requester
        testRequester = new User();
        testRequester.setUserId(1L);
        testRequester.setUserAccountType(UserAccountType.REQUESTER);
        testRequester.setToken("test-token");

        // Create test contract DTO
        testContractGetDTO = new ContractGetDTO();
        testContractGetDTO.setContractId(1L);
        testContractGetDTO.setRequesterId(1L);

        // Create test driver DTO
        testDriverDTO = new AuthenticatedDriverDTO();
        testDriverDTO.setUserId(2L);

        // Create test offer DTO
        testOfferGetDTO = new OfferGetDTO();
        testOfferGetDTO.setOfferId(1L);
        testOfferGetDTO.setOfferStatus(OfferStatus.CREATED);
        testOfferGetDTO.setContract(testContractGetDTO);
        testOfferGetDTO.setDriver(testDriverDTO);

        testOfferPostDTO = new OfferPostDTO();
        testOfferPostDTO.setContractId(1L);
        testOfferPostDTO.setDriverId(1L);

        testOfferPutDTO = new OfferPutDTO();
        testOfferPutDTO.setStatus(OfferStatus.ACCEPTED);
    }

    @Test
    public void getOffers_noFilters_success() {
        // given
        List<OfferGetDTO> offers = new ArrayList<>();
        when(offerService.getOffers(null, null, null)).thenReturn(offers);
        when(authorizationService.authenticateUser(anyLong(), anyString())).thenReturn(new User());

        // when
        ResponseEntity<Object> response = offerController.getOffers(1L, "token", null, null, null);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(offerService).getOffers(null, null, null);
    }

    @Test
    public void getOffers_withFilters_success() {
        // given
        List<OfferGetDTO> offers = new ArrayList<>();
        when(offerService.getOffers(1L, 1L, OfferStatus.CREATED)).thenReturn(offers);
        when(authorizationService.authenticateUser(anyLong(), anyString())).thenReturn(new User());

        // when
        ResponseEntity<Object> response = offerController.getOffers(1L, "token", 1L, 1L, OfferStatus.CREATED);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(offerService).getOffers(1L, 1L, OfferStatus.CREATED);
    }

    @Test
    public void getOffer_asDriver_success() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.getOffer(1L, 2L, "test-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testOfferGetDTO, responseBody.get("offer"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffer_asRequester_success() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.getOffer(1L, 1L, "test-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testOfferGetDTO, responseBody.get("offer"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffer_unauthorized() {
        // given
        when(authorizationService.authenticateUser(any(), any())).thenReturn(null);

        // when
        ResponseEntity<Object> response = offerController.getOffer(1L, 1L, "invalid-token");

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid credentials", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffer_forbidden_wrongDriver() {
        // given
        testDriver.setUserId(3L); // Different driver ID than the offer's driver
        when(authorizationService.authenticateUser(3L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.getOffer(1L, 3L, "test-token");

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You are not authorized to view this offer", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffer_forbidden_wrongRequester() {
        // given
        testRequester.setUserId(3L); // Different requester ID than the contract's requester
        when(authorizationService.authenticateUser(3L, "test-token")).thenReturn(testRequester);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.getOffer(1L, 3L, "test-token");

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You are not authorized to view this offer", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffer_notFound() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerController.getOffer(1L, 2L, "test-token"));
    }

    @Test
    public void createOffer_success() {
        // given
        when(offerService.createOffer(any())).thenReturn(testOfferGetDTO);

        // when
        OfferGetDTO response = offerController.createOffer(testOfferPostDTO);

        // then
        assertEquals(testOfferGetDTO.getOfferId(), response.getOfferId());
    }

    @Test
    public void createOffer_invalidInput_throwsException() {
        // given
        when(offerService.createOffer(any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerController.createOffer(testOfferPostDTO));
    }

    @Test
    public void deleteOffer_success() {
        // given
        doNothing().when(offerService).deleteOffer(any());

        // when
        offerController.deleteOffer(1L);

        // then
        verify(offerService, times(1)).deleteOffer(any());
    }

    @Test
    public void deleteOffer_notFound_throwsException() {
        // given
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(offerService).deleteOffer(any());

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerController.deleteOffer(1L));
    }

    @Test
    public void updateOfferStatus_success() {
        // given
        when(offerService.updateOfferStatus(any(), any())).thenReturn(testOfferGetDTO);

        // when
        OfferGetDTO response = offerController.updateOfferStatus(1L, testOfferPutDTO);

        // then
        assertEquals(testOfferGetDTO.getOfferId(), response.getOfferId());
    }

    @Test
    public void updateOfferStatus_invalidState_throwsException() {
        // given
        when(offerService.updateOfferStatus(any(), any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerController.updateOfferStatus(1L, testOfferPutDTO));
    }

    @Test
    public void getOffersByContract_success() {
        // given
        List<OfferGetDTO> offers = Collections.singletonList(testOfferGetDTO);
        when(offerService.getOffers(any(), any(), any())).thenReturn(offers);

        // when
        List<OfferGetDTO> response = offerController.getOffersByContract(1L);

        // then
        assertEquals(1, response.size());
        assertEquals(testOfferGetDTO.getOfferId(), response.get(0).getOfferId());
    }

    @Test
    public void getOffersByDriver_success() {
        // given
        List<OfferGetDTO> offers = Collections.singletonList(testOfferGetDTO);
        when(offerService.getOffers(any(), any(), any())).thenReturn(offers);
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);

        // when
        ResponseEntity<Object> response = offerController.getOffersByDriver(2L, 2L, "test-token", null);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(offers, responseBody.get("offers"));
        assertNotNull(responseBody.get("timestamp"));
        verify(offerService, times(1)).getOffers(null, 2L, null);
    }

    @Test
    public void updateOfferStatus_missingStatus_throwsException() {
        // given
        testOfferPutDTO.setStatus(null);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.updateOfferStatus(1L, testOfferPutDTO);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Status is required", exception.getReason());
    }

    @Test
    public void getOffersByContract_notFound_throwsException() {
        // given
        when(offerService.getOffers(any(), any(), any()))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found"));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.getOffersByContract(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void getOffersByDriver_withStatusFilter_success() {
        // given
        List<OfferGetDTO> offers = Collections.singletonList(testOfferGetDTO);
        when(offerService.getOffers(any(), any(), any())).thenReturn(offers);
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);

        // when
        ResponseEntity<Object> response = offerController.getOffersByDriver(2L, 2L, "test-token", OfferStatus.CREATED);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(offers, responseBody.get("offers"));
        assertNotNull(responseBody.get("timestamp"));
        verify(offerService, times(1)).getOffers(null, 2L, OfferStatus.CREATED);
    }

    @Test
    public void getOffersByDriver_unauthorized_returns401() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(null);

        // when
        ResponseEntity<Object> response = offerController.getOffersByDriver(2L, 2L, "test-token", null);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid credentials", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffersByDriver_forbidden_wrongDriver_returns403() {
        // given
        when(authorizationService.authenticateUser(3L, "test-token")).thenReturn(testDriver);

        // when
        ResponseEntity<Object> response = offerController.getOffersByDriver(2L, 3L, "test-token", null);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You are not authorized to view these offers", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffersByDriver_forbidden_requester_returns403() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);

        // when
        ResponseEntity<Object> response = offerController.getOffersByDriver(2L, 1L, "test-token", null);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Only drivers can view offers", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffersByDriver_notFound_throwsException() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffers(any(), any(), any()))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.getOffersByDriver(2L, 2L, "test-token", OfferStatus.CREATED);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void createOffer_conflict_throwsException() {
        // given
        when(offerService.createOffer(any()))
            .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Offer already exists"));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    public void deleteOffer_forbidden_throwsException() {
        // given
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete accepted offer"))
            .when(offerService).deleteOffer(any());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.deleteOffer(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    public void getOffers_unauthorized_returns401() {
        // given
        when(authorizationService.authenticateUser(anyLong(), anyString())).thenReturn(null);

        // when
        ResponseEntity<Object> response = offerController.getOffers(1L, "invalid-token", null, null, null);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals("Invalid credentials", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffers_asDriver_canOnlySeeOwnOffers() {
        // given
        User driver = new User();
        driver.setUserId(1L);
        driver.setUserAccountType(UserAccountType.DRIVER);
        when(authorizationService.authenticateUser(1L, "token")).thenReturn(driver);
        List<OfferGetDTO> offers = new ArrayList<>();
        when(offerService.getOffers(null, 1L, null)).thenReturn(offers);

        // when
        ResponseEntity<Object> response = offerController.getOffers(1L, "token", null, 2L, null);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(offerService).getOffers(null, 1L, null); // Should override driverId to 1L
    }

    @Test
    public void getOffers_asRequester_canOnlySeeOwnContracts() {
        // given
        User requester = new User();
        requester.setUserId(1L);
        requester.setUserAccountType(UserAccountType.REQUESTER);
        when(authorizationService.authenticateUser(1L, "token")).thenReturn(requester);
        
        Contract contract = new Contract();
        Requester otherRequester = new Requester();
        otherRequester.setUserId(2L);
        contract.setRequester(otherRequester);
        when(contractService.getContractById(2L)).thenReturn(contract);

        // when
        ResponseEntity<Object> response = offerController.getOffers(1L, "token", 2L, null, null);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals("You are not authorized to view offers for this contract", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    public void getOffers_asRequester_canSeeAllOwnContracts() {
        // given
        User requester = new User();
        requester.setUserId(1L);
        requester.setUserAccountType(UserAccountType.REQUESTER);
        when(authorizationService.authenticateUser(1L, "token")).thenReturn(requester);
        
        List<Contract> contracts = new ArrayList<>();
        Contract contract = new Contract();
        contract.setContractId(1L);
        Requester requesterEntity = new Requester();
        requesterEntity.setUserId(1L);
        contract.setRequester(requesterEntity);
        contracts.add(contract);
        when(contractService.getContractsByRequesterId(1L, null)).thenReturn(contracts);
        
        List<OfferGetDTO> offers = new ArrayList<>();
        OfferGetDTO offer = new OfferGetDTO();
        offer.setOfferId(1L);
        offers.add(offer);
        when(offerService.getOffers(1L, null, null)).thenReturn(offers);

        // when
        ResponseEntity<Object> response = offerController.getOffers(1L, "token", null, null, null);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("offers"));
        assertTrue(responseBody.containsKey("timestamp"));
        assertEquals(offers, responseBody.get("offers"));
        assertNotNull(responseBody.get("timestamp"));
        verify(offerService).getOffers(1L, null, null);
    }

    @Test
    public void getOffers_asDriver_withStatusFilter_success() {
        // given
        User driver = new User();
        driver.setUserId(1L);
        driver.setUserAccountType(UserAccountType.DRIVER);
        when(authorizationService.authenticateUser(1L, "token")).thenReturn(driver);
        List<OfferGetDTO> offers = new ArrayList<>();
        when(offerService.getOffers(null, 1L, OfferStatus.CREATED)).thenReturn(offers);

        // when
        ResponseEntity<Object> response = offerController.getOffers(1L, "token", null, null, OfferStatus.CREATED);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(offerService).getOffers(null, 1L, OfferStatus.CREATED);
    }

    @Test
    public void getOffers_asRequester_withStatusFilter_success() {
        // given
        User requester = new User();
        requester.setUserId(1L);
        requester.setUserAccountType(UserAccountType.REQUESTER);
        when(authorizationService.authenticateUser(1L, "token")).thenReturn(requester);
        
        List<Contract> contracts = new ArrayList<>();
        Contract contract = new Contract();
        contract.setContractId(1L);
        Requester requesterEntity = new Requester();
        requesterEntity.setUserId(1L);
        contract.setRequester(requesterEntity);
        contracts.add(contract);
        when(contractService.getContractsByRequesterId(1L, null)).thenReturn(contracts);
        
        List<OfferGetDTO> offers = new ArrayList<>();
        OfferGetDTO offer = new OfferGetDTO();
        offer.setOfferId(1L);
        offers.add(offer);
        when(offerService.getOffers(1L, null, OfferStatus.CREATED)).thenReturn(offers);

        // when
        ResponseEntity<Object> response = offerController.getOffers(1L, "token", null, null, OfferStatus.CREATED);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("offers"));
        assertTrue(responseBody.containsKey("timestamp"));
        assertEquals(offers, responseBody.get("offers"));
        assertNotNull(responseBody.get("timestamp"));
        verify(offerService).getOffers(1L, null, OfferStatus.CREATED);
    }
} 