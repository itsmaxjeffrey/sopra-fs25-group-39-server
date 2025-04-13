package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Offer;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.repository.OfferRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OfferService offerService;

    private Contract testContract;
    private Driver testDriver;
    private Offer testOffer;
    private OfferPostDTO testOfferPostDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test contract
        testContract = new Contract();
        testContract.setContractId(1L);
        testContract.setContractStatus(ContractStatus.REQUESTED);
        testContract.setTitle("Test Contract");
        testContract.setPrice(100.0f);
        testContract.setCollateral(150.0f);
        testContract.setMass(50.0f);
        testContract.setVolume(2.0f);
        testContract.setFragile(false);
        testContract.setCoolingRequired(false);
        testContract.setRideAlong(false);
        testContract.setManPower(1);
        testContract.setContractDescription("Test Description");
        testContract.setMoveDateTime(LocalDateTime.now().plusDays(1));

        // Create test driver
        testDriver = new Driver();
        testDriver.setUserId(1L);
        testDriver.setUsername("testdriver");
        testDriver.setEmail("driver@test.com");
        testDriver.setPassword("password");
        testDriver.setToken("1");

        // Create test offer
        testOffer = new Offer();
        testOffer.setOfferId(1L);
        testOffer.setContract(testContract);
        testOffer.setDriver(testDriver);
        testOffer.setOfferStatus(OfferStatus.CREATED);

        // Create test DTO
        testOfferPostDTO = new OfferPostDTO();
        testOfferPostDTO.setContractId(1L);
        testOfferPostDTO.setDriverId(1L);

        // Mock repository responses
        when(contractRepository.findById(any())).thenReturn(Optional.of(testContract));
        when(userRepository.findById(any())).thenReturn(Optional.of(testDriver));
        when(offerRepository.save(any())).thenReturn(testOffer);
        when(offerRepository.findByContract_ContractIdAndDriver_UserId(any(), any())).thenReturn(Collections.emptyList());
    }

    @Test
    public void createOffer_success() {
        // given
        when(contractRepository.findById(any())).thenReturn(Optional.of(testContract));
        when(userRepository.findById(any())).thenReturn(Optional.of(testDriver));
        when(offerRepository.findByContract_ContractIdAndDriver_UserId(any(), any()))
            .thenReturn(Collections.emptyList());
        when(offerRepository.save(any())).thenReturn(testOffer);

        // when
        OfferGetDTO createdOffer = offerService.createOffer(testOfferPostDTO);

        // then
        assertNotNull(createdOffer);
        assertEquals(testOffer.getOfferId(), createdOffer.getOfferId());
        assertEquals(testOffer.getOfferStatus(), createdOffer.getOfferStatus());
        verify(contractRepository, times(1)).save(any());
        verify(offerRepository, times(1)).save(any());
    }

    @Test
    public void createOffer_contractNotFound_throwsException() {
        // given
        when(contractRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Contract not found", exception.getReason());
    }

    @Test
    public void createOffer_driverNotFound_throwsException() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User not found", exception.getReason());
    }

    @Test
    public void createOffer_userNotDriver_throwsException() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(new Requester()));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User is not a driver", exception.getReason());
    }

    @Test
    public void createOffer_offerAlreadyExists_throwsException() {
        // given
        when(offerRepository.findByContract_ContractIdAndDriver_UserId(any(), any()))
            .thenReturn(Collections.singletonList(testOffer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("An offer already exists for this contract and driver", exception.getReason());
    }

    @Test
    public void createOffer_contractInInvalidState_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        when(contractRepository.findById(any())).thenReturn(Optional.of(testContract));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertTrue(exception.getReason().contains("Cannot create offer for a contract that is"));
    }

    @Test
    public void acceptOffer_success() {
        // given
        testContract.setContractStatus(ContractStatus.OFFERED);
        testOffer.setOfferStatus(OfferStatus.CREATED);
        
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));
        when(offerRepository.findByContract_ContractIdAndOfferStatus(any(), any()))
            .thenReturn(Collections.emptyList());
        when(contractRepository.save(any())).thenReturn(testContract);
        when(offerRepository.save(any())).thenReturn(testOffer);

        // when
        OfferGetDTO acceptedOffer = offerService.acceptOffer(1L);

        // then
        assertNotNull(acceptedOffer);
        assertEquals(OfferStatus.ACCEPTED, acceptedOffer.getOfferStatus());
        assertEquals(ContractStatus.ACCEPTED, testContract.getContractStatus());
        assertNotNull(testContract.getAcceptedOffer());
        assertNotNull(testContract.getAcceptedDateTime());
        verify(contractRepository, times(1)).save(any());
        verify(offerRepository, times(1)).save(any());
    }

    @Test
    public void acceptOffer_offerNotFound_throwsException() {
        // given
        when(offerRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.acceptOffer(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Offer not found", exception.getReason());
    }

    @Test
    public void acceptOffer_contractNotOffered_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.REQUESTED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.acceptOffer(1L);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Offers can only be accepted for OFFERED contracts", exception.getReason());
    }

    @Test
    public void rejectOffer_success() {
        // given
        testContract.setContractStatus(ContractStatus.OFFERED);
        testOffer.setOfferStatus(OfferStatus.CREATED);
        
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));
        when(offerRepository.findByContract_ContractIdAndOfferStatus(any(), any()))
            .thenReturn(Collections.emptyList());
        when(offerRepository.save(any())).thenReturn(testOffer);

        // when
        OfferGetDTO rejectedOffer = offerService.rejectOffer(1L);

        // then
        assertNotNull(rejectedOffer);
        assertEquals(OfferStatus.REJECTED, rejectedOffer.getOfferStatus());
        verify(offerRepository, times(1)).save(any());
    }

    @Test
    public void rejectOffer_offerNotFound_throwsException() {
        // given
        when(offerRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.rejectOffer(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Offer not found", exception.getReason());
    }

    @Test
    public void rejectOffer_contractNotRequestedOrOffered_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.rejectOffer(1L);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Offers can only be rejected for REQUESTED or OFFERED contracts", exception.getReason());
    }

    @Test
    public void deleteOffer_success() {
        // given
        testContract.setContractStatus(ContractStatus.OFFERED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));
        when(offerRepository.findByContract_ContractId(any())).thenReturn(Collections.emptyList());

        // when
        offerService.deleteOffer(1L);

        // then
        verify(offerRepository, times(1)).delete(any());
        verify(contractRepository, times(1)).save(any());
        assertEquals(ContractStatus.REQUESTED, testContract.getContractStatus());
    }

    @Test
    public void deleteOffer_offerNotFound_throwsException() {
        // given
        when(offerRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.deleteOffer(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Offer not found", exception.getReason());
    }

    @Test
    public void deleteOffer_acceptedOffer_throwsException() {
        // given
        testOffer.setOfferStatus(OfferStatus.ACCEPTED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.deleteOffer(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Cannot delete an accepted offer", exception.getReason());
    }

    @Test
    public void deleteOffer_rejectedOffer_throwsException() {
        // given
        testOffer.setOfferStatus(OfferStatus.REJECTED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.deleteOffer(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Cannot delete a rejected offer", exception.getReason());
    }

    @Test
    public void deleteOffer_acceptedContract_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.deleteOffer(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Cannot delete an offer for an accepted contract", exception.getReason());
    }

    @Test
    public void getOffers_success() {
        // given
        List<Offer> offers = Collections.singletonList(testOffer);
        when(offerRepository.findAll()).thenReturn(offers);

        // when
        List<OfferGetDTO> foundOffers = offerService.getOffers(null, null, null);

        // then
        assertEquals(1, foundOffers.size());
        assertEquals(testOffer.getOfferId(), foundOffers.get(0).getOfferId());
    }

    @Test
    public void getOffers_withFilters_success() {
        // given
        List<Offer> offers = Collections.singletonList(testOffer);
        when(offerRepository.findByContract_ContractIdAndDriver_UserIdAndOfferStatus(any(), any(), any()))
            .thenReturn(offers);

        // when
        List<OfferGetDTO> foundOffers = offerService.getOffers(1L, 1L, OfferStatus.CREATED);

        // then
        assertEquals(1, foundOffers.size());
        assertEquals(testOffer.getOfferId(), foundOffers.get(0).getOfferId());
    }

    @Test
    public void getOffer_success() {
        // given
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when
        OfferGetDTO foundOffer = offerService.getOffer(1L);

        // then
        assertNotNull(foundOffer);
        assertEquals(testOffer.getOfferId(), foundOffer.getOfferId());
    }

    @Test
    public void getOffer_notFound_throwsException() {
        // given
        when(offerRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.getOffer(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Offer not found", exception.getReason());
    }
} 