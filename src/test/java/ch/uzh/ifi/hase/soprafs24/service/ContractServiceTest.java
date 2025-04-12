package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Location;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private UserRepository userRepository;

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

        List<Contract> allContracts = Arrays.asList(requestedContract, acceptedContract);
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
} 