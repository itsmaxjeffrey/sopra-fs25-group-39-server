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

public class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private ContractService contractService;

    private Contract testContract;
    private Requester testRequester;
    private Location testFromLocation;
    private Location testToLocation;

    @BeforeEach
    public void setup() {
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

    /*@Test
    public void createContract_validInputs_success() {
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
        assertEquals(testContract.getFragile(), createdContract.getFragile());
        assertEquals(testContract.getCoolingRequired(), createdContract.getCoolingRequired());
        assertEquals(testContract.getRideAlong(), createdContract.getRideAlong());
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
    */

    @Test
    public void createContract_requesterNotFound_throwsException() {
        // given
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.empty());

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    public void getContracts_success() {
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
    public void getContractById_success() {
        // given
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when
        Contract foundContract = contractService.getContractById(1L);

        // then
        assertEquals(testContract, foundContract);
    }

    @Test
    public void getContractById_notFound_throwsException() {
        // given
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.empty());

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.getContractById(1L));
    }

    @Test
    public void getContractsByUser_success() {
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
    public void getContractsByStatus_success() {
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
    public void getContractsByRequesterId_withStatus_success() {
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
    public void getContractsByRequesterId_withoutStatus_success() {
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
    public void getContractsByDriverId_withStatus_success() {
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
    public void getContractsByDriverId_withoutStatus_success() {
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
    public void getContractsByStatus_allStatuses_success() {
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
    public void getContractsByUser_withStatus_success() {
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
    public void getContractsByUser_withoutStatus_success() {
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
    public void getContractsByRequesterId_emptyList_success() {
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
    public void getContractsByDriverId_multipleContracts_success() {
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
    public void getContractsByStatus_mixedStatuses_success() {
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
    public void getContractsByUser_mixedStatuses_success() {
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
    public void getContractsByUser_invalidUserId_throwsException() {
        // given
        Mockito.when(contractRepository.findByRequester_UserId(Mockito.any()))
            .thenReturn(Collections.emptyList());

        // when/then
        List<Contract> contracts = contractService.getContractsByUser(999L, ContractStatus.REQUESTED);
        assertTrue(contracts.isEmpty());
    }

    @Test
    public void updateContract_onlyRequestedContractsCanBeUpdated() {
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
    public void updateContract_requestedContract_success() {
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
    public void cancelContract_acceptedContract_success() {
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
    public void cancelContract_tooCloseToMoveDate_throwsException() {
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
    public void cancelContract_nonAcceptedStatus_throwsException() {
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
    public void fulfillContract_asRequester_success() {
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
    public void fulfillContract_asDriver_success() {
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
    public void fulfillContract_invalidStatus_throwsException() {
        // given
        Contract existingContract = new Contract();
        existingContract.setContractId(1L);
        existingContract.setContractStatus(ContractStatus.REQUESTED);

        Mockito.when(contractRepository.findById(1L)).thenReturn(java.util.Optional.of(existingContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.fulfillContract(1L));
    }

    @Test
    public void deleteContract_requestedStatus_success() {
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
    public void deleteContract_offeredStatus_success() {
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
    public void deleteContract_acceptedStatus_success() {
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
    public void deleteContract_acceptedStatus_tooCloseToMoveDate_throwsException() {
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
    public void deleteContract_alreadyDeleted_throwsException() {
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
    public void deleteContract_completedOrFinalized_throwsException() {
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
    public void createContract_negativePrice_throwsException() {
        // given
        testContract.setPrice(-100.0f);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    public void createContract_negativeCollateral_throwsException() {
        // given
        testContract.setCollateral(-50.0f);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    public void createContract_negativeMass_throwsException() {
        // given
        testContract.setMass(-10.0f);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    public void createContract_negativeVolume_throwsException() {
        // given
        testContract.setVolume(-5.0f);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    public void createContract_negativeManpower_throwsException() {
        // given
        testContract.setManPower(-2);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    public void createContract_pastMoveDate_throwsException() {
        // given
        testContract.setMoveDateTime(LocalDateTime.now().minusDays(1));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    public void createContract_missingAddresses_throwsException() {
        // given
        testContract.setFromAddress(null);
        testContract.setToAddress(null);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testRequester));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.createContract(testContract));
    }

    @Test
    public void updateContract_invalidStatus_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        Contract contractUpdates = new Contract();
        contractUpdates.setContractStatus(ContractStatus.COMPLETED);
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.updateContract(1L, contractUpdates));
    }

    @Test
    public void updateContract_pastMoveDate_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.REQUESTED);
        Contract contractUpdates = new Contract();
        contractUpdates.setMoveDateTime(LocalDateTime.now().minusDays(1));
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.updateContract(1L, contractUpdates));
    }

    @Test
    public void updateContract_invalidRequester_throwsException() {
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
    public void completeContract_beforeMoveDate_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        testContract.setMoveDateTime(LocalDateTime.now().plusDays(1));
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.completeContract(1L));
    }

    @Test
    public void completeContract_afterMoveDate_success() {
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
    public void finalizeContract_nonCompleted_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.finalizeContract(1L));
    }

    @Test
    public void cancelContract_within72Hours_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        testContract.setMoveDateTime(LocalDateTime.now().plusHours(48));
        Mockito.when(contractRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(testContract));

        // when/then -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> contractService.cancelContract(1L, "Test reason"));
    }

    @Test
    public void cancelContract_moreThan72Hours_success() {
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
    public void getContractById_withDriver_success() {
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
    public void getContractsByDriverId_withDriver_success() {
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
    public void getContractsByDriverId_withoutDriver_success() {
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
} 