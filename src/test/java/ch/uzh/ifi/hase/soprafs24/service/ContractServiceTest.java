package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Offer;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.OfferRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractFilterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private GoogleMapsService googleMapsService;

    @InjectMocks
    private ContractService contractService;

    private Contract testContract;
    private Requester testRequester;
    private Location testFromLocation;
    private Location testToLocation;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test requester
        testRequester = new Requester();
        testRequester.setUserId(1L);
        testRequester.setUsername("testuser");
        testRequester.setEmail("test@example.com");
        testRequester.setFirstName("Test");
        testRequester.setLastName("User");

        // Create test locations
        testFromLocation = new Location();
        testFromLocation.setId(1L);
        testFromLocation.setFormattedAddress("Test From Address");
        testFromLocation.setLatitude(47.3769);
        testFromLocation.setLongitude(8.5417);

        testToLocation = new Location();
        testToLocation.setId(2L);
        testToLocation.setFormattedAddress("Test To Address");
        testToLocation.setLatitude(47.3770);
        testToLocation.setLongitude(8.5418);

        // Create test contract
        testContract = new Contract();
        testContract.setContractId(1L);
        testContract.setTitle("Test Contract");
        testContract.setMass(10.0f);
        testContract.setVolume(5.0f);
        testContract.setFragile(false);
        testContract.setCoolingRequired(false);
        testContract.setRideAlong(false);
        testContract.setManPower(2);
        testContract.setContractDescription("Test Description");
        testContract.setPrice(100.0f);
        testContract.setCollateral(50.0f);
        testContract.setMoveDateTime(LocalDateTime.now().plusDays(1));
        testContract.setContractStatus(ContractStatus.REQUESTED);
        testContract.setRequester(testRequester);
        testContract.setFromAddress(testFromLocation);
        testContract.setToAddress(testToLocation);

        // when -> any object is being save in the contractRepository -> return the dummy testContract
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(testContract);
        Mockito.when(offerRepository.findByContract_ContractId(Mockito.any())).thenReturn(Collections.emptyList());
    }

    @Test
    void createContract_validInputs_success() {
        // given
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when
        Contract createdContract = contractService.createContract(testContract);

        // then
        Mockito.verify(contractRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.any());

        assertEquals(testContract.getContractId(), createdContract.getContractId());
        assertEquals(testContract.getTitle(), createdContract.getTitle());
        assertEquals(testContract.getMass(), createdContract.getMass());
        assertEquals(testContract.getVolume(), createdContract.getVolume());
        assertEquals(testContract.isFragile(), createdContract.isFragile());
        assertEquals(testContract.isCoolingRequired(), createdContract.isCoolingRequired());
        assertEquals(testContract.isRideAlong(), createdContract.isRideAlong());
        assertEquals(testContract.getManPower(), createdContract.getManPower());
        assertEquals(testContract.getContractDescription(), createdContract.getContractDescription());
        assertEquals(testContract.getPrice(), createdContract.getPrice());
        assertEquals(testContract.getCollateral(), createdContract.getCollateral());
        assertEquals(testContract.getMoveDateTime(), createdContract.getMoveDateTime());
        assertEquals(ContractStatus.REQUESTED, createdContract.getContractStatus());
        assertEquals(testRequester, createdContract.getRequester());
        assertEquals(testFromLocation, createdContract.getFromAddress());
        assertEquals(testToLocation, createdContract.getToAddress());
    }

    @Test
    void createContract_requesterNotFound_throwsException() {
        // given
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.empty());

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    void getContracts_success() {
        // given
        List<Contract> allContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findAll()).thenReturn(allContracts);

        // when
        List<Contract> foundContracts = contractService.getContracts();

        // then
        assertEquals(allContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
    }

    @Test
    void getContractById_success() {
        // given
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when
        Contract foundContract = contractService.getContractById(1L);

        // then
        assertEquals(testContract, foundContract);
    }

    @Test
    void getContractById_notFound_throwsException() {
        // given
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.empty());

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.getContractById(1L));
    }

    @Test
    void getContractsByUser_success() {
        // given
        List<Contract> userContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByRequester_UserId(Mockito.any())).thenReturn(userContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByUser(1L, ContractStatus.REQUESTED);

        // then
        assertEquals(userContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
    }

    @Test
    void getContractsByStatus_success() {
        // given
        List<Contract> statusContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByContractStatus(Mockito.any())).thenReturn(statusContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByStatus(ContractStatus.REQUESTED);

        // then
        assertEquals(statusContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
    }

    @Test
    void getContractsByRequesterId_withStatus_success() {
        // given
        List<Contract> requesterContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByRequester_UserIdAndContractStatus(Mockito.any(), Mockito.any()))
            .thenReturn(requesterContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByRequesterId(1L, ContractStatus.REQUESTED);

        // then
        assertEquals(requesterContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
        Mockito.verify(contractRepository, Mockito.times(1))
            .findByRequester_UserIdAndContractStatus(1L, ContractStatus.REQUESTED);
    }

    @Test
    void getContractsByRequesterId_withoutStatus_success() {
        // given
        List<Contract> requesterContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByRequester_UserId(Mockito.any()))
            .thenReturn(requesterContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByRequesterId(1L, null);

        // then
        assertEquals(requesterContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
        Mockito.verify(contractRepository, Mockito.times(1))
            .findByRequester_UserId(1L);
    }

    @Test
    void getContractsByDriverId_withStatus_success() {
        // given
        List<Contract> driverContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByDriver_UserIdAndContractStatus(Mockito.any(), Mockito.any()))
            .thenReturn(driverContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByDriverId(1L, ContractStatus.ACCEPTED);

        // then
        assertEquals(driverContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
        Mockito.verify(contractRepository, Mockito.times(1))
            .findByDriver_UserIdAndContractStatus(1L, ContractStatus.ACCEPTED);
    }

    @Test
    void getContractsByDriverId_withoutStatus_success() {
        // given
        List<Contract> driverContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByDriver_UserId(Mockito.any()))
            .thenReturn(driverContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByDriverId(1L, null);

        // then
        assertEquals(driverContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
        Mockito.verify(contractRepository, Mockito.times(1))
            .findByDriver_UserId(1L);
    }

    @Test
    void getContractsByStatus_allStatuses_success() {
        // given
        List<Contract> statusContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByContractStatus(Mockito.any()))
            .thenReturn(statusContracts);

        // test all possible statuses
        for (ContractStatus status : ContractStatus.values()) {
            // when
            List<Contract> foundContracts = contractService.getContractsByStatus(status);

            // then
            assertEquals(statusContracts.size(), foundContracts.size());
            assertEquals(testContract, foundContracts.get(0));
            Mockito.verify(contractRepository, Mockito.times(1))
                .findByContractStatus(status);
        }
    }

    @Test
    void getContractsByUser_withStatus_success() {
        // given
        List<Contract> userContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByRequester_UserId(Mockito.any()))
            .thenReturn(userContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByUser(1L, ContractStatus.REQUESTED);

        // then
        assertEquals(1, foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
        Mockito.verify(contractRepository, Mockito.times(1))
            .findByRequester_UserId(1L);
    }

    @Test
    void getContractsByUser_withoutStatus_success() {
        // given
        List<Contract> userContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByRequester_UserId(Mockito.any()))
            .thenReturn(userContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByUser(1L, null);

        // then
        assertEquals(userContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
        Mockito.verify(contractRepository, Mockito.times(1))
            .findByRequester_UserId(1L);
    }

    @Test
    void getContractsByRequesterId_emptyList_success() {
        // given
        List<Contract> emptyList = Collections.emptyList();
        Mockito.when(contractRepository.findByRequester_UserIdAndContractStatus(Mockito.any(), Mockito.any()))
            .thenReturn(emptyList);

        // when
        List<Contract> foundContracts = contractService.getContractsByRequesterId(1L, ContractStatus.REQUESTED);

        // then
        assertTrue(foundContracts.isEmpty());
        Mockito.verify(contractRepository, Mockito.times(1))
            .findByRequester_UserIdAndContractStatus(1L, ContractStatus.REQUESTED);
    }

    @Test
    void getContractsByDriverId_multipleContracts_success() {
        // given
        Contract contract1 = new Contract();
        contract1.setContractId(1L);
        contract1.setContractStatus(ContractStatus.ACCEPTED);

        Contract contract2 = new Contract();
        contract2.setContractId(2L);
        contract2.setContractStatus(ContractStatus.ACCEPTED);

        List<Contract> driverContracts = Arrays.asList(contract1, contract2);
        Mockito.when(contractRepository.findByDriver_UserIdAndContractStatus(Mockito.any(), Mockito.any()))
            .thenReturn(driverContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByDriverId(1L, ContractStatus.ACCEPTED);

        // then
        assertEquals(2, foundContracts.size());
        assertTrue(foundContracts.contains(contract1));
        assertTrue(foundContracts.contains(contract2));
    }

    @Test
    void getContractsByStatus_mixedStatuses_success() {
        // given
        Contract requestedContract = new Contract();
        requestedContract.setContractId(1L);
        requestedContract.setContractStatus(ContractStatus.REQUESTED);

        Contract acceptedContract = new Contract();
        acceptedContract.setContractId(2L);
        acceptedContract.setContractStatus(ContractStatus.ACCEPTED);

        Mockito.when(contractRepository.findByContractStatus(ContractStatus.REQUESTED))
            .thenReturn(Collections.singletonList(requestedContract));
        Mockito.when(contractRepository.findByContractStatus(ContractStatus.ACCEPTED))
            .thenReturn(Collections.singletonList(acceptedContract));

        // when
        List<Contract> requestedContracts = contractService.getContractsByStatus(ContractStatus.REQUESTED);
        List<Contract> acceptedContracts = contractService.getContractsByStatus(ContractStatus.ACCEPTED);

        // then
        assertEquals(1, requestedContracts.size());
        assertEquals(requestedContract, requestedContracts.get(0));
        assertEquals(1, acceptedContracts.size());
        assertEquals(acceptedContract, acceptedContracts.get(0));
    }

    @Test
    void getContractsByUser_mixedStatuses_success() {
        // given
        Contract requestedContract = new Contract();
        requestedContract.setContractId(1L);
        requestedContract.setContractStatus(ContractStatus.REQUESTED);

        Contract acceptedContract = new Contract();
        acceptedContract.setContractId(2L);
        acceptedContract.setContractStatus(ContractStatus.ACCEPTED);

        List<Contract> allContracts = Arrays.asList(requestedContract, acceptedContract);
        Mockito.when(contractRepository.findByRequester_UserId(Mockito.any()))
            .thenReturn(allContracts);

        // when
        List<Contract> requestedContracts = contractService.getContractsByUser(1L, ContractStatus.REQUESTED);
        List<Contract> acceptedContracts = contractService.getContractsByUser(1L, ContractStatus.ACCEPTED);

        // then
        assertEquals(1, requestedContracts.size());
        assertEquals(requestedContract, requestedContracts.get(0));
        assertEquals(1, acceptedContracts.size());
        assertEquals(acceptedContract, acceptedContracts.get(0));
    }

    @Test
    void getContractsByUser_invalidUserId_throwsException() {
        // given
        Mockito.when(contractRepository.findByRequester_UserId(Mockito.any()))
            .thenReturn(Collections.emptyList());

        // when/then
        List<Contract> contracts = contractService.getContractsByUser(999L, ContractStatus.REQUESTED);
        assertTrue(contracts.isEmpty());
    }

    @Test
    void updateContract_onlyRequestedContractsCanBeUpdated() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.ACCEPTED); // Not REQUESTED

        Contract contractUpdates = new Contract();
        contractUpdates.setTitle("Updated Title");

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.updateContract(1L, contractUpdates));
    }

    @Test
    void updateContract_requestedContract_success() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.REQUESTED);
        existingContract.setTitle("Original Title");
        existingContract.setMoveDateTime(LocalDateTime.now().plusDays(1));

        Contract contractUpdates = new Contract();
        contractUpdates.setTitle("Updated Title");
        contractUpdates.setMoveDateTime(LocalDateTime.now().plusDays(2));

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(existingContract);

        // when
        Contract updatedContract = contractService.updateContract(1L, contractUpdates);

        // then
        assertEquals("Updated Title", updatedContract.getTitle());
        assertEquals(ContractStatus.REQUESTED, updatedContract.getContractStatus());
    }

    @Test
    void cancelContract_acceptedContract_success() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.ACCEPTED);
        existingContract.setMoveDateTime(LocalDateTime.now().plusDays(4)); // More than 72 hours in future
        existingContract.setRequester(testRequester);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(existingContract);

        // when
        Contract cancelledContract = contractService.cancelContract(1L, "Cancellation reason");

        // then
        assertEquals(ContractStatus.CANCELED, cancelledContract.getContractStatus());
        assertEquals("Cancellation reason", cancelledContract.getCancelReason());
    }

    @Test
    void cancelContract_tooCloseToMoveDate_throwsException() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.ACCEPTED);
        existingContract.setMoveDateTime(LocalDateTime.now().plusHours(48)); // Less than 72 hours

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.cancelContract(1L, "Cancellation reason"));
    }

    @Test
    void cancelContract_nonAcceptedStatus_throwsException() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.REQUESTED);
        existingContract.setMoveDateTime(LocalDateTime.now().plusDays(4));

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.cancelContract(1L, "Cancellation reason"));
    }

    @Test
    void fulfillContract_asRequester_success() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.COMPLETED);
        existingContract.setRequester(testRequester);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(existingContract);

        // when
        Contract fulfilledContract = contractService.fulfillContract(1L);

        // then
        assertEquals(ContractStatus.FINALIZED, fulfilledContract.getContractStatus());
    }

    @Test
    void fulfillContract_asDriver_success() {
        // given
        Driver testDriver = new Driver();
        testDriver.setUserId(2L);

        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.COMPLETED);
        existingContract.setDriver(testDriver);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(existingContract);

        // when
        Contract fulfilledContract = contractService.fulfillContract(1L);

        // then
        assertEquals(ContractStatus.FINALIZED, fulfilledContract.getContractStatus());
    }

    @Test
    void fulfillContract_invalidStatus_throwsException() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.REQUESTED);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.fulfillContract(1L));
    }

    @Test
    void deleteContract_requestedStatus_success() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.REQUESTED);
        existingContract.setRequester(testRequester);

        // Mock repository behavior
        Mockito.when(contractRepository.findById(1L)).thenReturn(Optional.of(existingContract));
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(existingContract);
        Mockito.when(offerRepository.findByContract_ContractId(1L)).thenReturn(new ArrayList<>());

        // when
        contractService.deleteContract(1L);

        // then
        // Verify contract was marked as DELETED first
        Mockito.verify(contractRepository).save(Mockito.argThat(contract -> 
            contract.getContractStatus() == ContractStatus.DELETED
        ));
        
        // Verify repository interactions
        Mockito.verify(contractRepository).findById(1L);
        Mockito.verify(offerRepository).findByContract_ContractId(1L);
        Mockito.verify(contractRepository).flush();
        Mockito.verify(offerRepository).flush();
    }

    @Test
    void deleteContract_offeredStatus_success() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.OFFERED);
        existingContract.setRequester(testRequester);

        // Mock offers for this contract
        List<Offer> offers = new ArrayList<>();
        Offer offer1 = new Offer();
        offer1.setOfferStatus(OfferStatus.CREATED);
        offers.add(offer1);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));
        Mockito.when(offerRepository.findByContract_ContractId(1L)).thenReturn(offers);
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(existingContract);
        Mockito.when(offerRepository.save(Mockito.any())).thenReturn(offer1);

        // when
        contractService.deleteContract(1L);

        // then
        // Verify contract was marked as DELETED first
        Mockito.verify(contractRepository).save(Mockito.argThat(contract -> 
            contract.getContractStatus() == ContractStatus.DELETED
        ));
        
        // Verify all offers were rejected
        Mockito.verify(offerRepository).save(Mockito.argThat(offer -> 
            offer.getOfferStatus() == OfferStatus.REJECTED
        ));
        
        // Verify repository interactions
        Mockito.verify(contractRepository).findById(1L);
        Mockito.verify(offerRepository).findByContract_ContractId(1L);
        Mockito.verify(contractRepository).flush();
        Mockito.verify(offerRepository).flush();
    }

    @Test
    void deleteContract_acceptedStatus_success() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.ACCEPTED);
        existingContract.setRequester(testRequester);
        existingContract.setMoveDateTime(LocalDateTime.now().plusDays(4)); // More than 72 hours in future

        // Mock offers for this contract
        List<Offer> offers = new ArrayList<>();
        Offer acceptedOffer = new Offer();
        acceptedOffer.setOfferStatus(OfferStatus.ACCEPTED);
        offers.add(acceptedOffer);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));
        Mockito.when(offerRepository.findByContract_ContractId(1L)).thenReturn(offers);
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(existingContract);
        Mockito.when(offerRepository.save(Mockito.any())).thenReturn(acceptedOffer);

        // when
        contractService.deleteContract(1L);

        // then
        // Verify contract was marked as DELETED first
        Mockito.verify(contractRepository).save(Mockito.argThat(contract -> 
            contract.getContractStatus() == ContractStatus.DELETED
        ));
        
        // Verify all offers were rejected
        Mockito.verify(offerRepository).save(Mockito.argThat(offer -> 
            offer.getOfferStatus() == OfferStatus.REJECTED
        ));
        
        // Verify repository interactions
        Mockito.verify(contractRepository).findById(1L);
        Mockito.verify(offerRepository).findByContract_ContractId(1L);
        Mockito.verify(contractRepository).flush();
        Mockito.verify(offerRepository).flush();
    }

    @Test
    void deleteContract_acceptedStatus_tooCloseToMoveDate_throwsException() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.ACCEPTED);
        existingContract.setRequester(testRequester);
        existingContract.setMoveDateTime(LocalDateTime.now().plusHours(48)); // Less than 72 hours

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> contractService.deleteContract(1L));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Cannot delete an accepted contract less than 72 hours before move date", 
            exception.getReason());
    }

    @Test
    void deleteContract_alreadyDeleted_throwsException() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.DELETED);
        existingContract.setRequester(testRequester);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> contractService.deleteContract(1L));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Contract is already deleted", exception.getReason());
    }

    @Test
    void deleteContract_completedOrFinalized_throwsException() {
        // Test for COMPLETED status
        Contract completedContract = new Contract();
        completedContract.setContractId(1L);
        completedContract.setContractStatus(ContractStatus.COMPLETED);
        completedContract.setRequester(testRequester);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(completedContract));

        ResponseStatusException completedException = assertThrows(ResponseStatusException.class, 
            () -> contractService.deleteContract(1L));
        assertEquals(HttpStatus.CONFLICT, completedException.getStatus());
        assertEquals("Cannot delete a completed or finalized contract", completedException.getReason());

        // Test for FINALIZED status
        Contract finalizedContract = new Contract();
        finalizedContract.setContractId(1L);
        finalizedContract.setContractStatus(ContractStatus.FINALIZED);
        finalizedContract.setRequester(testRequester);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(finalizedContract));

        ResponseStatusException finalizedException = assertThrows(ResponseStatusException.class, 
            () -> contractService.deleteContract(1L));
        assertEquals(HttpStatus.CONFLICT, finalizedException.getStatus());
        assertEquals("Cannot delete a completed or finalized contract", finalizedException.getReason());
    }

    @Test
    void createContract_negativePrice_throwsException() {
        // given
        testContract.setPrice(-100.0f);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    void createContract_negativeCollateral_throwsException() {
        // given
        testContract.setCollateral(-50.0f);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    void createContract_negativeMass_throwsException() {
        // given
        testContract.setMass(-10.0f);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    void createContract_negativeVolume_throwsException() {
        // given
        testContract.setVolume(-5.0f);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    void createContract_negativeManpower_throwsException() {
        // given
        testContract.setManPower(-2);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    // @Test
    // void createContract_pastMoveDate_throwsException() {
    //     // given
    //     testContract.setMoveDateTime(LocalDateTime.now().minusDays(1));
    //     Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

    //     // when/then -> check that an error is thrown
    //     assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    // }

    @Test
    void createContract_missingAddresses_throwsException() {
        // given
        testContract.setFromAddress(null);
        testContract.setToAddress(null);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    void updateContract_invalidStatus_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        Contract contractUpdates = new Contract();
        contractUpdates.setContractStatus(ContractStatus.COMPLETED);
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.updateContract(1L, contractUpdates));
    }

    @Test
    void updateContract_pastMoveDate_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.REQUESTED);
        Contract contractUpdates = new Contract();
        contractUpdates.setMoveDateTime(LocalDateTime.now().minusDays(1));
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.updateContract(1L, contractUpdates));
    }

    @Test
    void updateContract_invalidRequester_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.REQUESTED);
        Contract contractUpdates = new Contract();
        Requester differentRequester = new Requester();
        differentRequester.setUserId(999L); // Different from testRequester's ID
        contractUpdates.setRequester(differentRequester);
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.updateContract(1L, contractUpdates));
    }

    @Test
    void completeContract_beforeMoveDate_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        testContract.setMoveDateTime(LocalDateTime.now().plusDays(1));
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.completeContract(1L));
    }

    @Test
    void completeContract_afterMoveDate_success() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        testContract.setMoveDateTime(LocalDateTime.now().minusDays(1));
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(testContract);

        // when
        Contract completedContract = contractService.completeContract(1L);

        // then
        assertEquals(ContractStatus.COMPLETED, completedContract.getContractStatus());
    }

    @Test
    void finalizeContract_nonCompleted_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.finalizeContract(1L));
    }

    @Test
    void cancelContract_within72Hours_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        testContract.setMoveDateTime(LocalDateTime.now().plusHours(48));
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.cancelContract(1L, "Test reason"));
    }

    @Test
    void cancelContract_moreThan72Hours_success() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        testContract.setMoveDateTime(LocalDateTime.now().plusDays(4));
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(testContract);

        // when
        Contract cancelledContract = contractService.cancelContract(1L, "Test reason");

        // then
        assertEquals(ContractStatus.CANCELED, cancelledContract.getContractStatus());
        assertEquals("Test reason", cancelledContract.getCancelReason());
    }

    @Test
    void getContractById_withDriver_success() {
        // given
        Driver driver = new Driver();
        driver.setUserId(2L);
        testContract.setDriver(driver);
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when
        Contract foundContract = contractService.getContractById(1L);

        // then
        assertEquals(testContract, foundContract);
        assertEquals(2L, foundContract.getDriver().getUserId());
    }

    @Test
    void getContractsByDriverId_withDriver_success() {
        // given
        Driver driver = new Driver();
        driver.setUserId(2L);
        testContract.setDriver(driver);
        List<Contract> driverContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByDriver_UserIdAndContractStatus(Mockito.any(), Mockito.any()))
            .thenReturn(driverContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByDriverId(2L, ContractStatus.ACCEPTED);

        // then
        assertEquals(driverContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
        assertEquals(2L, foundContracts.get(0).getDriver().getUserId());
        Mockito.verify(contractRepository, Mockito.times(1))
            .findByDriver_UserIdAndContractStatus(2L, ContractStatus.ACCEPTED);
    }

    @Test
    void getContractsByDriverId_withoutDriver_success() {
        // given
        testContract.setDriver(null);
        List<Contract> driverContracts = Arrays.asList(testContract);
        Mockito.when(contractRepository.findByDriver_UserId(Mockito.any()))
            .thenReturn(driverContracts);

        // when
        List<Contract> foundContracts = contractService.getContractsByDriverId(1L, null);

        // then
        assertEquals(driverContracts.size(), foundContracts.size());
        assertEquals(testContract, foundContracts.get(0));
        assertNull(foundContracts.get(0).getDriver());
        Mockito.verify(contractRepository, Mockito.times(1))
            .findByDriver_UserId(1L);
    }

    @Test
    void handleDriverDeletion_success() {
        // given
        Driver driver = new Driver();
        driver.setUserId(2L);
        
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setContractStatus(ContractStatus.OFFERED);
        contract.setRequester(testRequester);
        
        Offer offer = new Offer();
        offer.setOfferId(1L);
        offer.setOfferStatus(OfferStatus.CREATED);
        offer.setDriver(driver);
        offer.setContract(contract);
        
        List<Offer> offers = Collections.singletonList(offer);
        Mockito.when(offerRepository.findByDriver_UserId(2L)).thenReturn(offers);
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(contract);
        Mockito.when(offerRepository.save(Mockito.any())).thenReturn(offer);

        // when
        contractService.handleDriverDeletion(2L);

        // then
        Mockito.verify(offerRepository).save(Mockito.argThat(o -> 
            o.getOfferStatus() == OfferStatus.REJECTED
        ));
        Mockito.verify(contractRepository).save(Mockito.argThat(c -> 
            c.getContractStatus() == ContractStatus.REQUESTED
        ));
        Mockito.verify(offerRepository).flush();
        Mockito.verify(contractRepository).flush();
    }

    @Test
    void handleRequesterDeletion_success() {
        // given
        Contract contract = new Contract();
        contract.setContractId(1L);
        contract.setContractStatus(ContractStatus.REQUESTED);
        contract.setRequester(testRequester);
        
        Offer offer = new Offer();
        offer.setOfferId(1L);
        offer.setOfferStatus(OfferStatus.CREATED);
        offer.setContract(contract);
        
        List<Contract> contracts = Collections.singletonList(contract);
        List<Offer> offers = Collections.singletonList(offer);
        
        Mockito.when(contractRepository.findByRequester_UserId(1L)).thenReturn(contracts);
        Mockito.when(offerRepository.findByContract_ContractId(1L)).thenReturn(offers);
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(contract);
        Mockito.when(offerRepository.save(Mockito.any())).thenReturn(offer);

        // when
        contractService.handleRequesterDeletion(1L);

        // then
        Mockito.verify(offerRepository).save(Mockito.argThat(o -> 
            o.getOfferStatus() == OfferStatus.REJECTED
        ));
        Mockito.verify(contractRepository).save(Mockito.argThat(c -> 
            c.getContractStatus() == ContractStatus.DELETED
        ));
        Mockito.verify(offerRepository).flush();
        Mockito.verify(contractRepository).flush();
    }

    @Test
    void getContracts_withFilters_success() {
        // Setup
        ContractFilterDTO filter = new ContractFilterDTO();
        filter.setPrice(100.0);
        filter.setWeight(50.0);
        filter.setHeight(2.0);
        filter.setLength(3.0);
        filter.setWidth(1.0);

        Contract contract = new Contract();
        contract.setPrice(100.0f);
        contract.setMass(50.0f);
        contract.setVolume(6.0f); // height * length * width = 2 * 3 * 1 = 6

        Mockito.when(contractRepository.findAll()).thenReturn(Arrays.asList(contract));

        // Execute
        List<Contract> result = contractService.getContracts(47.3769, 8.5417, filter);

        // Verify
        assertEquals(1, result.size());
        assertEquals(contract, result.get(0));
    }

    @Test
    void getContracts_withFilters_noMatches() {
        // Setup
        ContractFilterDTO filter = new ContractFilterDTO();
        filter.setPrice(50.0); // Set price lower than contract's price

        Contract contract = new Contract();
        contract.setPrice(100.0f);
        contract.setMass(50.0f);
        contract.setVolume(6.0f);

        Mockito.when(contractRepository.findAll()).thenReturn(Arrays.asList(contract));

        // Execute
        List<Contract> result = contractService.getContracts(47.3769, 8.5417, filter);

        // Verify
        assertTrue(result.isEmpty());
    }

    @Test
    void updateContractStatuses_success() {
        // given
        Contract acceptedContract = new Contract();
        acceptedContract.setContractId(1L);
        acceptedContract.setContractStatus(ContractStatus.ACCEPTED);
        acceptedContract.setMoveDateTime(LocalDateTime.now().minusDays(1));

        Contract requestedContract = new Contract();
        requestedContract.setContractId(2L);
        requestedContract.setContractStatus(ContractStatus.REQUESTED);
        requestedContract.setMoveDateTime(LocalDateTime.now().minusDays(1));

        Contract offeredContract = new Contract();
        offeredContract.setContractId(3L);
        offeredContract.setContractStatus(ContractStatus.OFFERED);
        offeredContract.setMoveDateTime(LocalDateTime.now().minusDays(1));

        Offer offer = new Offer();
        offer.setOfferId(1L);
        offer.setOfferStatus(OfferStatus.CREATED);
        offer.setContract(offeredContract);

        Mockito.when(contractRepository.findByContractStatusAndMoveDateTimeBefore(
            eq(ContractStatus.ACCEPTED), Mockito.any())).thenReturn(Collections.singletonList(acceptedContract));
        Mockito.when(contractRepository.findByContractStatusAndMoveDateTimeBefore(
            eq(ContractStatus.REQUESTED), Mockito.any())).thenReturn(Collections.singletonList(requestedContract));
        Mockito.when(contractRepository.findByContractStatusAndMoveDateTimeBefore(
            eq(ContractStatus.OFFERED), Mockito.any())).thenReturn(Collections.singletonList(offeredContract));
        Mockito.when(offerRepository.findByContract_ContractId(Mockito.any())).thenReturn(Collections.singletonList(offer));
        Mockito.when(contractRepository.saveAll(Mockito.any())).thenReturn(Arrays.asList(acceptedContract, requestedContract, offeredContract));
        Mockito.when(offerRepository.save(Mockito.any())).thenReturn(offer);

        // when
        contractService.updateContractStatuses();

        // then
        Mockito.verify(contractRepository).saveAll(Mockito.argThat(contracts -> {
            for (Contract c : contracts) {
                if (c.getContractStatus() == ContractStatus.COMPLETED) {
                    return true;
                }
            }
            return false;
        }));
        Mockito.verify(contractRepository).saveAll(Mockito.argThat(contracts -> {
            for (Contract c : contracts) {
                if (c.getContractStatus() != ContractStatus.CANCELED) {
                    return false;
                }
            }
            return true;
        }));
        Mockito.verify(offerRepository).save(Mockito.argThat(o -> 
            o.getOfferStatus() == OfferStatus.REJECTED
        ));
        Mockito.verify(contractRepository).flush();
        Mockito.verify(offerRepository).flush();
    }

    @Test
    void validateContractData_validInputs_success() {
        // given
        Contract validContract = new Contract();
        validContract.setPrice(100.0f);
        validContract.setCollateral(50.0f);
        validContract.setMass(10.0f);
        validContract.setVolume(5.0f);
        validContract.setManPower(2);
        validContract.setMoveDateTime(LocalDateTime.now().plusDays(1));
        validContract.setFromAddress(testFromLocation);
        validContract.setToAddress(testToLocation);
        validContract.setRequester(testRequester);

        // Mock user repository to return the test requester
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testRequester));

        // when/then -> no exception should be thrown
        assertDoesNotThrow(() -> contractService.createContract(validContract));
    }

    @Test
    void validateContractUpdate_validInputs_success() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.REQUESTED);
        existingContract.setRequester(testRequester);
        existingContract.setMoveDateTime(LocalDateTime.now().plusDays(2));

        Contract contractUpdates = new Contract();
        contractUpdates.setTitle("Updated Title");
        contractUpdates.setMoveDateTime(LocalDateTime.now().plusDays(3));
        contractUpdates.setRequester(testRequester);

        Mockito.when(contractRepository.findById(1L)).thenReturn(Optional.of(existingContract));
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(existingContract);

        // when/then -> no exception should be thrown
        assertDoesNotThrow(() -> contractService.updateContract(1L, contractUpdates));
    }

    @Test
    void updateContract_partialUpdates_success() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setTitle("Original Title");
        existingContract.setMass(100.0f);
        existingContract.setVolume(10.0f);
        existingContract.setFragile(false);
        existingContract.setCoolingRequired(false);
        existingContract.setRideAlong(false);
        existingContract.setManPower(2);
        existingContract.setContractDescription("Original Description");
        existingContract.setPrice(100.0f);
        existingContract.setCollateral(50.0f);
        existingContract.setMoveDateTime(LocalDateTime.now().plusDays(1));
        existingContract.setContractStatus(ContractStatus.REQUESTED);

        Contract partialUpdates = new Contract();
        partialUpdates.setTitle("Updated Title");
        partialUpdates.setMass(200.0f);
        // Other fields remain null/unchanged

        Mockito.when(contractRepository.findById(1L)).thenReturn(Optional.of(existingContract));
        Mockito.when(contractRepository.save(Mockito.any())).thenReturn(existingContract);

        // when
        Contract updatedContract = contractService.updateContract(1L, partialUpdates);

        // then
        assertEquals("Updated Title", updatedContract.getTitle());
        assertEquals(200.0f, updatedContract.getMass());
        assertEquals(10.0f, updatedContract.getVolume()); // Should remain unchanged
        assertFalse(updatedContract.isFragile()); // Should remain unchanged
        assertEquals(2, updatedContract.getManPower()); // Should remain unchanged
        assertEquals("Original Description", updatedContract.getContractDescription()); // Should remain unchanged
        assertEquals(100.0f, updatedContract.getPrice()); // Should remain unchanged
        assertEquals(50.0f, updatedContract.getCollateral()); // Should remain unchanged
        assertEquals(ContractStatus.REQUESTED, updatedContract.getContractStatus()); // Should remain unchanged
    }

    @Test
    void getContracts_withLocationFilter_success() {
        // given
        Contract contract1 = new Contract();
        contract1.setFromAddress(testFromLocation);
        contract1.setPrice(100.0f);
        contract1.setMass(50.0f);
        contract1.setVolume(5.0f);
        contract1.setManPower(2);
        contract1.setFragile(false);
        contract1.setCoolingRequired(false);
        contract1.setRideAlong(false);
        contract1.setMoveDateTime(LocalDateTime.now().plusDays(1));

        Contract contract2 = new Contract();
        contract2.setFromAddress(testToLocation);
        contract2.setPrice(200.0f);
        contract2.setMass(100.0f);
        contract2.setVolume(10.0f);
        contract2.setManPower(3);
        contract2.setFragile(true);
        contract2.setCoolingRequired(true);
        contract2.setRideAlong(true);
        contract2.setMoveDateTime(LocalDateTime.now().plusDays(2));

        List<Contract> allContracts = Arrays.asList(contract1, contract2);

        ContractFilterDTO filters = new ContractFilterDTO();
        filters.setRadius(1.0); // 1km radius

        Mockito.when(contractRepository.findAll()).thenReturn(allContracts);
        Mockito.when(googleMapsService.calculateDistance(47.3769, 8.5417, 47.3769, 8.5417))
            .thenReturn(0.5); // Within radius
        Mockito.when(googleMapsService.calculateDistance(47.3769, 8.5417, 47.3770, 8.5418))
            .thenReturn(2.0); // Outside radius

        // when
        List<Contract> result = contractService.getContracts(47.3769, 8.5417, filters);

        // then
        assertEquals(1, result.size());
        assertTrue(result.contains(contract1));
        assertFalse(result.contains(contract2));

        verify(contractRepository).findAll();
        verify(googleMapsService, times(2)).calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void getContracts_withAllFilters_success() {
        // given
        Contract contract1 = new Contract();
        contract1.setFromAddress(testFromLocation);
        contract1.setPrice(100.0f);
        contract1.setMass(50.0f);
        contract1.setVolume(5.0f);
        contract1.setManPower(2);
        contract1.setFragile(false);
        contract1.setCoolingRequired(false);
        contract1.setRideAlong(false);
        contract1.setMoveDateTime(LocalDateTime.now().plusDays(1));

        Contract contract2 = new Contract();
        contract2.setFromAddress(testToLocation);
        contract2.setPrice(200.0f);
        contract2.setMass(100.0f);
        contract2.setVolume(10.0f);
        contract2.setManPower(3);
        contract2.setFragile(true);
        contract2.setCoolingRequired(true);
        contract2.setRideAlong(true);
        contract2.setMoveDateTime(LocalDateTime.now().plusDays(2));

        List<Contract> allContracts = Arrays.asList(contract1, contract2);

        ContractFilterDTO filters = new ContractFilterDTO();
        filters.setPrice(250.0); // Increased to allow both contracts
        filters.setWeight(150.0); // Increased to allow both contracts
        filters.setHeight(3.0); // Increased to allow both contracts
        filters.setLength(3.0); // Increased to allow both contracts
        filters.setWidth(3.0); // Increased to allow both contracts
        filters.setRequiredPeople(3); // Increased to allow both contracts
        filters.setFragile(null); // Set to null to not filter by fragile
        filters.setCoolingRequired(null); // Set to null to not filter by cooling
        filters.setRideAlong(null); // Set to null to not filter by ride along
        filters.setMoveDate(null); // Set to null to not filter by date
        filters.setRadius(1.0);

        Mockito.when(contractRepository.findAll()).thenReturn(allContracts);
        Mockito.when(googleMapsService.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(0.5) // First call for contract1
            .thenReturn(2.0); // Second call for contract2

        // when
        List<Contract> result = contractService.getContracts(47.3769, 8.5417, filters);

        // then
        assertEquals(1, result.size());
        assertTrue(result.contains(contract1));
        assertFalse(result.contains(contract2));

        verify(contractRepository).findAll();
        verify(googleMapsService, times(2)).calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }
} 