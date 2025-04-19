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
import ch.uzh.ifi.hase.soprafs24.rest.mapper.ContractDTOMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.OfferDTOMapper;
import org.springframework.test.util.ReflectionTestUtils;

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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class OfferServiceTest {

    @Mock
    private OfferDTOMapper offerDTOMapper;

    @Mock 
    private ContractDTOMapper contractDTOMapper;

    private OfferGetDTO testOfferGetDTO;
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
    void setup() {
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

        testOfferGetDTO = new OfferGetDTO();
        testOfferGetDTO.setOfferId(1L);
        testOfferGetDTO.setOfferStatus(OfferStatus.CREATED);
        
        // Configure the mappers to return expected DTOs
        when(offerDTOMapper.convertEntityToOfferGetDTO(any(Offer.class)))
            .thenReturn(testOfferGetDTO);
            
        // Set the mapper in your service using reflection
        ReflectionTestUtils.setField(offerService, "offerDTOMapper", offerDTOMapper);

        // Mock repository responses
        when(contractRepository.findById(any())).thenReturn(Optional.of(testContract));
        when(userRepository.findById(any())).thenReturn(Optional.of(testDriver));
        when(offerRepository.findByContract_ContractIdAndDriver_UserId(any(), any())).thenReturn(Collections.emptyList());
    }

    @Test
    void createOffer_success() {
        // given
        // Create a complete offer to be returned by the repository
        Offer savedOffer = new Offer();
        savedOffer.setOfferId(1L);
        savedOffer.setContract(testContract);
        savedOffer.setDriver(testDriver);
        savedOffer.setOfferStatus(OfferStatus.CREATED);
        
        // Create a new offer that will be returned by the mapper
        Offer newOffer = new Offer();
        
        // Mock the convertOfferPostDTOtoEntity method
        when(offerDTOMapper.convertOfferPostDTOtoEntity(any(OfferPostDTO.class)))
            .thenReturn(newOffer);
        
        when(offerRepository.save(any())).thenReturn(savedOffer);
        
        // For the mapper, make sure to always return a valid DTO
        OfferGetDTO offerDTO = new OfferGetDTO();
        offerDTO.setOfferId(1L);
        offerDTO.setOfferStatus(OfferStatus.CREATED);
        
        // Use this mock configuration instead of the one in setup()
        when(offerDTOMapper.convertEntityToOfferGetDTO(any(Offer.class))).thenReturn(offerDTO);

        // when
        OfferGetDTO createdOffer = offerService.createOffer(testOfferPostDTO);

        // then
        assertNotNull(createdOffer);
        assertEquals(offerDTO.getOfferId(), createdOffer.getOfferId());
        assertEquals(offerDTO.getOfferStatus(), createdOffer.getOfferStatus());
        verify(contractRepository, times(1)).save(any());
        verify(offerRepository, times(1)).save(any());
    }
    @Test
    void createOffer_contractNotFound_throwsException() {
        // given
        when(contractRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertEquals("Contract not found", reason);
    }

    @Test
    void createOffer_driverNotFound_throwsException() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertEquals("User not found", reason);
    }

    @Test
    void createOffer_userNotDriver_throwsException() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(new Requester()));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertEquals("User is not a driver", reason);
    }

    @Test
    void createOffer_offerAlreadyExists_throwsException() {
        // given
        when(offerRepository.findByContract_ContractIdAndDriver_UserId(any(), any()))
            .thenReturn(Collections.singletonList(testOffer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertEquals("An offer already exists for this contract and driver", reason);
    }

    @Test
    void createOffer_contractInInvalidState_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        when(contractRepository.findById(any())).thenReturn(Optional.of(testContract));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.createOffer(testOfferPostDTO);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertTrue(reason.contains("Cannot create offer for a contract that is"));
    }

    @Test
    void acceptOffer_success() {
        // given
        testContract.setContractStatus(ContractStatus.OFFERED);
        testOffer.setOfferStatus(OfferStatus.CREATED);
        
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));
        when(offerRepository.findByContract_ContractIdAndOfferStatus(any(), any()))
            .thenReturn(Collections.emptyList());
        
        // Configure the save behavior to update the offer status
        when(offerRepository.save(any())).thenAnswer(invocation -> {
            Offer savedOffer = invocation.getArgument(0);
            testOffer.setOfferStatus(savedOffer.getOfferStatus());
            return testOffer;
        });
        
        // Create a DTO that will be returned when offer is ACCEPTED
        OfferGetDTO acceptedDTO = new OfferGetDTO();
        acceptedDTO.setOfferId(1L);
        acceptedDTO.setOfferStatus(OfferStatus.ACCEPTED);
        
        // Override the original mapper configuration
        when(offerDTOMapper.convertEntityToOfferGetDTO(argThat(offer -> 
            offer.getOfferStatus() == OfferStatus.ACCEPTED))).thenReturn(acceptedDTO);

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
    void acceptOffer_offerNotFound_throwsException() {
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
    void acceptOffer_contractNotOffered_throwsException() {
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
    void rejectOffer_success() {
        // given
        testContract.setContractStatus(ContractStatus.OFFERED);
        testOffer.setOfferStatus(OfferStatus.CREATED);
        
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));
        when(offerRepository.findByContract_ContractIdAndOfferStatus(any(), any()))
            .thenReturn(Collections.emptyList());
        
        // Configure the save behavior to update the offer status
        when(offerRepository.save(any())).thenAnswer(invocation -> {
            Offer savedOffer = invocation.getArgument(0);
            testOffer.setOfferStatus(savedOffer.getOfferStatus());
            return testOffer;
        });
        
        // Create a DTO that will be returned when offer is REJECTED
        OfferGetDTO rejectedDTO = new OfferGetDTO();
        rejectedDTO.setOfferId(1L);
        rejectedDTO.setOfferStatus(OfferStatus.REJECTED);
        
        // Override the original mapper configuration
        when(offerDTOMapper.convertEntityToOfferGetDTO(argThat(offer -> 
            offer.getOfferStatus() == OfferStatus.REJECTED))).thenReturn(rejectedDTO);

        // when
        OfferGetDTO rejectedOffer = offerService.rejectOffer(1L);

        // then
        assertNotNull(rejectedOffer);
        assertEquals(OfferStatus.REJECTED, rejectedOffer.getOfferStatus());
        verify(offerRepository, times(1)).save(any());
    }

    @Test
    void rejectOffer_offerNotFound_throwsException() {
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
    void rejectOffer_contractNotRequestedOrOffered_throwsException() {
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
    void deleteOffer_success() {
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
    void deleteOffer_offerNotFound_throwsException() {
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
    void deleteOffer_acceptedOffer_throwsException() {
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
    void deleteOffer_rejectedOffer_throwsException() {
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
    void deleteOffer_acceptedContract_throwsException() {
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
    void getOffers_success() {
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
    void getOffers_withFilters_success() {
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
    void getOffer_success() {
        // given
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when
        OfferGetDTO foundOffer = offerService.getOffer(1L);

        // then
        assertNotNull(foundOffer);
        assertEquals(testOffer.getOfferId(), foundOffer.getOfferId());
    }

    @Test
    void getOffer_notFound_throwsException() {
        // given
        when(offerRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.getOffer(1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Offer not found", exception.getReason());
    }

    @Test
    void testUpdateOfferStatus_OfferNotFound() {
        // given
        Long offerId = 1L;
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.updateOfferStatus(offerId, OfferStatus.ACCEPTED);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Offer not found", exception.getReason());
    }

    @Test
    void testUpdateOfferStatus_NotInCreatedState() {
        // given
        Long offerId = 1L;
        Offer offer = new Offer();
        offer.setOfferId(offerId);
        offer.setOfferStatus(OfferStatus.ACCEPTED);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.updateOfferStatus(offerId, OfferStatus.REJECTED);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Only CREATED offers can be modified", exception.getReason());
    }

    @Test
    void testUpdateOfferStatus_AcceptOffer_ContractNotOffered() {
        // given
        Long offerId = 1L;
        Offer offer = new Offer();
        offer.setOfferId(offerId);
        offer.setOfferStatus(OfferStatus.CREATED);
        
        Contract contract = new Contract();
        contract.setContractStatus(ContractStatus.REQUESTED);
        offer.setContract(contract);
        
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.updateOfferStatus(offerId, OfferStatus.ACCEPTED);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Offers can only be accepted for OFFERED contracts", exception.getReason());
    }

    @Test
    void testUpdateOfferStatus_AcceptOffer_Success() {
        // given
        Long offerId = 1L;
        Long contractId = 2L;
        
        // Setup main offer
        Offer offer = new Offer();
        offer.setOfferId(offerId);
        offer.setOfferStatus(OfferStatus.CREATED);
        
        Contract contract = new Contract();
        contract.setContractId(contractId);
        contract.setContractStatus(ContractStatus.OFFERED);
        offer.setContract(contract);
        
        // Setup other offers to be rejected
        Offer otherOffer1 = new Offer();
        otherOffer1.setOfferId(3L);
        otherOffer1.setOfferStatus(OfferStatus.CREATED);
        otherOffer1.setContract(contract);
        
        Offer otherOffer2 = new Offer();
        otherOffer2.setOfferId(4L);
        otherOffer2.setOfferStatus(OfferStatus.CREATED);
        otherOffer2.setContract(contract);
        
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerRepository.findByContract_ContractIdAndOfferStatus(contractId, OfferStatus.CREATED))
            .thenReturn(Arrays.asList(otherOffer1, otherOffer2));
        when(offerRepository.save(any(Offer.class))).thenAnswer(i -> i.getArgument(0));
        when(contractRepository.save(any(Contract.class))).thenAnswer(i -> i.getArgument(0));
        when(offerDTOMapper.convertEntityToOfferGetDTO(any(Offer.class))).thenReturn(new OfferGetDTO());

        // when
        OfferGetDTO result = offerService.updateOfferStatus(offerId, OfferStatus.ACCEPTED);

        // then
        assertNotNull(result);
        assertEquals(OfferStatus.ACCEPTED, offer.getOfferStatus());
        assertEquals(ContractStatus.ACCEPTED, contract.getContractStatus());
        assertEquals(offer, contract.getAcceptedOffer());
        assertNotNull(contract.getAcceptedDateTime());
        
        // Verify other offers were rejected
        assertEquals(OfferStatus.REJECTED, otherOffer1.getOfferStatus());
        assertEquals(OfferStatus.REJECTED, otherOffer2.getOfferStatus());
        
        verify(offerRepository, times(3)).save(any(Offer.class)); // main offer + 2 other offers
        verify(contractRepository).save(contract);
        verify(offerDTOMapper).convertEntityToOfferGetDTO(offer);
    }

    @Test
    void testAcceptOffer_OfferNotFound() {
        // given
        Long offerId = 1L;
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.acceptOffer(offerId);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Offer not found", exception.getReason());
    }

    @Test
    void testAcceptOffer_ContractNotOffered() {
        // given
        Long offerId = 1L;
        Offer offer = new Offer();
        offer.setOfferId(offerId);
        
        Contract contract = new Contract();
        contract.setContractStatus(ContractStatus.REQUESTED);
        offer.setContract(contract);
        
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            offerService.acceptOffer(offerId);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Offers can only be accepted for OFFERED contracts", exception.getReason());
    }

    @Test
    void testAcceptOffer_Success() {
        // given
        Long offerId = 1L;
        Long contractId = 2L;
        
        // Setup main offer
        Offer offer = new Offer();
        offer.setOfferId(offerId);
        
        Contract contract = new Contract();
        contract.setContractId(contractId);
        contract.setContractStatus(ContractStatus.OFFERED);
        offer.setContract(contract);
        
        // Setup other offers to be rejected
        Offer otherOffer1 = new Offer();
        otherOffer1.setOfferId(3L);
        otherOffer1.setOfferStatus(OfferStatus.CREATED);
        otherOffer1.setContract(contract);
        
        Offer otherOffer2 = new Offer();
        otherOffer2.setOfferId(4L);
        otherOffer2.setOfferStatus(OfferStatus.CREATED);
        otherOffer2.setContract(contract);
        
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerRepository.findByContract_ContractIdAndOfferStatus(contractId, OfferStatus.CREATED))
            .thenReturn(Arrays.asList(otherOffer1, otherOffer2));
        when(offerRepository.save(any(Offer.class))).thenAnswer(i -> i.getArgument(0));
        when(contractRepository.save(any(Contract.class))).thenAnswer(i -> i.getArgument(0));
        when(offerDTOMapper.convertEntityToOfferGetDTO(any(Offer.class))).thenReturn(new OfferGetDTO());

        // when
        OfferGetDTO result = offerService.acceptOffer(offerId);

        // then
        assertNotNull(result);
        assertEquals(OfferStatus.ACCEPTED, offer.getOfferStatus());
        assertEquals(ContractStatus.ACCEPTED, contract.getContractStatus());
        assertEquals(offer, contract.getAcceptedOffer());
        assertNotNull(contract.getAcceptedDateTime());
        
        // Verify other offers were rejected
        assertEquals(OfferStatus.REJECTED, otherOffer1.getOfferStatus());
        assertEquals(OfferStatus.REJECTED, otherOffer2.getOfferStatus());
        
        verify(offerRepository, times(3)).save(any(Offer.class)); // main offer + 2 other offers
        verify(contractRepository).save(contract);
        verify(offerDTOMapper).convertEntityToOfferGetDTO(offer);
    }
}