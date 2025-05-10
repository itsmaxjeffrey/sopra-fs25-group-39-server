package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
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

class OfferControllerTest {

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
    private Contract testContract;
    private Contract testAcceptedContract;
    private Requester testRequesterEntity;

    @BeforeEach
    void setup() {
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

        // Create test requester entity
        testRequesterEntity = new Requester();
        testRequesterEntity.setUserId(1L);

        // Create test contract
        testContract = new Contract();
        testContract.setContractId(1L);
        testContract.setContractStatus(ContractStatus.REQUESTED);
        testContract.setRequester(testRequesterEntity);

        // Create test accepted contract
        testAcceptedContract = new Contract();
        testAcceptedContract.setContractId(2L);
        testAcceptedContract.setContractStatus(ContractStatus.ACCEPTED);
        testAcceptedContract.setRequester(testRequesterEntity);

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
    void getOffers_noFilters_success() {
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
    void getOffers_withFilters_success() {
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
    void getOffer_asDriver_success() {
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
    void getOffer_asRequester_success() {
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
    void getOffer_unauthorized() {
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
    void getOffer_forbidden_wrongDriver() {
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
    void getOffer_forbidden_wrongRequester() {
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
    void getOffer_notFound() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerController.getOffer(1L, 2L, "test-token"));
    }

    @Test
    void createOffer_success() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(contractService.getContractById(anyLong())).thenReturn(testContract);
        when(offerService.createOffer(any(OfferPostDTO.class))).thenReturn(testOfferGetDTO);
        testOfferPostDTO.setDriverId(2L); // Set driver ID to match authenticated user

        // when
        ResponseEntity<Object> response = offerController.createOffer(2L, "test-token", testOfferPostDTO);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testOfferGetDTO, responseBody.get("offer"));
        assertEquals("Offer created successfully", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void createOffer_unauthorized_returns401() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(null);

        // when
        ResponseEntity<Object> response = offerController.createOffer(2L, "test-token", testOfferPostDTO);

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
    void deleteOffer_success() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        doNothing().when(offerService).deleteOffer(1L);

        // when
        ResponseEntity<Object> response = offerController.deleteOffer(1L, 2L, "test-token");

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Offer deleted successfully", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void deleteOffer_unauthorized_returns401() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(null);

        // when
        ResponseEntity<Object> response = offerController.deleteOffer(1L, 2L, "test-token");

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
    void updateOfferStatus_success() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        when(offerService.updateOfferStatus(1L, OfferStatus.DELETED)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.updateOfferStatus(1L, OfferStatus.DELETED, 2L, "test-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testOfferGetDTO, responseBody.get("offer"));
        assertEquals("Offer status updated successfully", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void updateOfferStatus_unauthorized_returns401() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(null);

        // when
        ResponseEntity<Object> response = offerController.updateOfferStatus(1L, OfferStatus.DELETED, 2L, "test-token");

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
    void getOffersByContract_success() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);
        when(offerService.getOffers(1L, null, null)).thenReturn(Collections.singletonList(testOfferGetDTO));
        
        // Create a contract owned by the requester
        Contract contract = new Contract();
        Requester requester = new Requester();
        requester.setUserId(1L);
        contract.setRequester(requester);
        when(contractService.getContractById(1L)).thenReturn(contract);

        // when
        ResponseEntity<Object> response = offerController.getOffersByContract(1L, 1L, "test-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(Collections.singletonList(testOfferGetDTO), responseBody.get("offers"));
        assertEquals("Offers retrieved successfully", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void getOffersByContract_unauthorized_returns401() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(null);

        // when
        ResponseEntity<Object> response = offerController.getOffersByContract(1L, 1L, "test-token");

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
    void createOffer_requesterCannotCreateOffer_forbidden() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);

        // when
        ResponseEntity<Object> response = offerController.createOffer(1L, "test-token", testOfferPostDTO);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Requesters cannot create offers", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void getOffersByDriver_success() {
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
    void getOffersByDriver_withStatusFilter_success() {
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
    void getOffersByDriver_unauthorized_returns401() {
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
    void getOffersByDriver_forbidden_wrongDriver_returns403() {
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
    void getOffersByDriver_forbidden_requester_returns403() {
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
    void getOffersByDriver_notFound_throwsException() {
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
    void getOffers_unauthorized_returns401() {
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
    void getOffers_asDriver_canOnlySeeOwnOffers() {
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
    void getOffers_asRequester_canOnlySeeOwnContracts() {
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
    void getOffers_asRequester_canSeeAllOwnContracts() {
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
    void getOffers_asDriver_withStatusFilter_success() {
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
    void getOffers_asRequester_withStatusFilter_success() {
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

    @Test
    void updateOfferStatus_driverCanDeleteOwnOffer_success() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        when(offerService.updateOfferStatus(1L, OfferStatus.DELETED)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.updateOfferStatus(1L, OfferStatus.DELETED, 2L, "test-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testOfferGetDTO, responseBody.get("offer"));
        assertEquals("Offer status updated successfully", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void updateOfferStatus_driverCannotAcceptOwnOffer_forbidden() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.updateOfferStatus(1L, OfferStatus.ACCEPTED, 2L, "test-token");

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Drivers can only delete their offers", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void updateOfferStatus_requesterCanAcceptOwnContractOffer_success() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        when(offerService.updateOfferStatus(1L, OfferStatus.ACCEPTED)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.updateOfferStatus(1L, OfferStatus.ACCEPTED, 1L, "test-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testOfferGetDTO, responseBody.get("offer"));
        assertEquals("Offer status updated successfully", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void updateOfferStatus_requesterCannotDeleteOffer_forbidden() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.updateOfferStatus(1L, OfferStatus.DELETED, 1L, "test-token");

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Requesters can only accept offers", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void deleteOffer_driverCanDeleteOwnOffer_success() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        doNothing().when(offerService).deleteOffer(1L);

        // when
        ResponseEntity<Object> response = offerController.deleteOffer(1L, 2L, "test-token");

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Offer deleted successfully", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void deleteOffer_requesterCannotDeleteOffer_forbidden() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);

        // when
        ResponseEntity<Object> response = offerController.deleteOffer(1L, 1L, "test-token");

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Requesters cannot delete offers", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void deleteOffer_driverCannotDeleteOtherDriverOffer_forbidden() {
        // given
        User otherDriver = new User();
        otherDriver.setUserId(3L);
        otherDriver.setUserAccountType(UserAccountType.DRIVER);
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        
        // Create an offer owned by a different driver
        OfferGetDTO otherDriverOffer = new OfferGetDTO();
        otherDriverOffer.setOfferId(1L);
        otherDriverOffer.setOfferStatus(OfferStatus.CREATED);
        AuthenticatedDriverDTO otherDriverDTO = new AuthenticatedDriverDTO();
        otherDriverDTO.setUserId(3L);
        otherDriverOffer.setDriver(otherDriverDTO);
        when(offerService.getOffer(1L)).thenReturn(otherDriverOffer);

        // when
        ResponseEntity<Object> response = offerController.deleteOffer(1L, 2L, "test-token");

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You are not authorized to delete this offer", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void getOffersByContract_forbidden_wrongRequester() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testRequester);
        
        Contract contract = new Contract();
        Requester otherRequester = new Requester();
        otherRequester.setUserId(3L);
        contract.setRequester(otherRequester);
        when(contractService.getContractById(1L)).thenReturn(contract);

        // when
        ResponseEntity<Object> response = offerController.getOffersByContract(1L, 2L, "test-token");

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You are not authorized to view offers for this contract", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void getOffersByContract_forbidden_invalidContractState() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        
        Contract contract = new Contract();
        contract.setContractStatus(ContractStatus.ACCEPTED);
        when(contractService.getContractById(1L)).thenReturn(contract);

        // when
        ResponseEntity<Object> response = offerController.getOffersByContract(1L, 2L, "test-token");

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You are not authorized to view offers for this contract", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void createOffer_badRequest_invalidContractState() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(contractService.getContractById(anyLong())).thenReturn(testAcceptedContract);
        when(offerService.createOffer(any(OfferPostDTO.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create offer for a contract that is accepted"));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.createOffer(2L, "test-token", testOfferPostDTO);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot create offer for a contract that is accepted", exception.getReason());
    }

    @Test
    void createOffer_conflict_duplicateOffer() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(contractService.getContractById(anyLong())).thenReturn(testContract);
        when(offerService.createOffer(any(OfferPostDTO.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "An offer already exists for this contract and driver"));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.createOffer(2L, "test-token", testOfferPostDTO);
        });
        
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("An offer already exists for this contract and driver", exception.getReason());
    }

    @Test
    void deleteOffer_forbidden_acceptedOffer() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete an accepted offer"))
            .when(offerService).deleteOffer(1L);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.deleteOffer(1L, 2L, "test-token");
        });
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Cannot delete an accepted offer", exception.getReason());
    }

    @Test
    void deleteOffer_notFound() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.deleteOffer(1L, 2L, "test-token");
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Offer not found", exception.getReason());
    }

    @Test
    void updateOfferStatus_badRequest_invalidContractState() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        when(offerService.updateOfferStatus(1L, OfferStatus.ACCEPTED))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offers can only be accepted for OFFERED contracts"));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerController.updateOfferStatus(1L, OfferStatus.ACCEPTED, 1L, "test-token");
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Offers can only be accepted for OFFERED contracts", exception.getReason());
    }

    @Test
    void getOfferDriver_asRequester_success() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        testContractGetDTO.setRequesterId(1L); // requester is owner
        testOfferGetDTO.setContract(testContractGetDTO);
        testOfferGetDTO.setDriver(testDriverDTO);

        // when
        ResponseEntity<Object> response = offerController.getOfferDriver(1L, 1L, "test-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testDriverDTO, responseBody.get("driver"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void getOfferDriver_asDriver_success() {
        // given
        when(authorizationService.authenticateUser(2L, "test-token")).thenReturn(testDriver);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        testDriverDTO.setUserId(2L);
        testOfferGetDTO.setDriver(testDriverDTO);
        testContractGetDTO.setRequesterId(1L);
        testOfferGetDTO.setContract(testContractGetDTO);

        // when
        ResponseEntity<Object> response = offerController.getOfferDriver(1L, 2L, "test-token");

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(testDriverDTO, responseBody.get("driver"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void getOfferDriver_unauthorized_returns401() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(null);

        // when
        ResponseEntity<Object> response = offerController.getOfferDriver(1L, 1L, "test-token");

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
    void getOfferDriver_forbidden_returns403() {
        // given
        User otherUser = new User();
        otherUser.setUserId(99L);
        otherUser.setUserAccountType(UserAccountType.REQUESTER);
        when(authorizationService.authenticateUser(99L, "test-token")).thenReturn(otherUser);
        when(offerService.getOffer(1L)).thenReturn(testOfferGetDTO);
        testContractGetDTO.setRequesterId(1L);
        testOfferGetDTO.setContract(testContractGetDTO);
        testDriverDTO.setUserId(2L);
        testOfferGetDTO.setDriver(testDriverDTO);

        // when
        ResponseEntity<Object> response = offerController.getOfferDriver(1L, 99L, "test-token");

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You are not authorized to view driver details for this offer", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }

    @Test
    void getOfferDriver_notFound_returns404() {
        // given
        when(authorizationService.authenticateUser(1L, "test-token")).thenReturn(testRequester);
        OfferGetDTO offerNoDriver = new OfferGetDTO();
        offerNoDriver.setOfferId(1L);
        offerNoDriver.setContract(testContractGetDTO);
        offerNoDriver.setDriver(null); // no driver assigned
        when(offerService.getOffer(1L)).thenReturn(offerNoDriver);

        // when
        ResponseEntity<Object> response = offerController.getOfferDriver(1L, 1L, "test-token");

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("No driver assigned to this offer", responseBody.get("message"));
        assertNotNull(responseBody.get("timestamp"));
    }
} 