package ch.uzh.ifi.hase.soprafs24.service;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Offer;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.repository.OfferRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.ContractDTOMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.OfferDTOMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.OfferDTOMapperImpl;

public class OfferServiceTest {
    @Mock
    private ContractDTOMapper contractDTOMapper;

    @Mock
    private OfferDTOMapper offerDTOMapper;
    
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
    private OfferGetDTO testOfferGetDTO;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test contract
        testContract = new Contract();
        testContract.setContractId(1L);
        testContract.setContractStatus(ContractStatus.REQUESTED);
        
        // Create test driver
        testDriver = new Driver();
        testDriver.setUserId(1L);
        testDriver.setUsername("testdriver");
        
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
        
        // Create test offer get DTO for mapper mock responses
        testOfferGetDTO = new OfferGetDTO();
        testOfferGetDTO.setOfferId(1L);
        testOfferGetDTO.setOfferStatus(OfferStatus.CREATED);
        
        // IMPORTANT: Instead of mocking, use the real mapper but set its contractDTOMapper field
        // This is the key fix
        OfferDTOMapperImpl offerMapper = new OfferDTOMapperImpl();
        
        // Use reflection to set the contractDTOMapper field
        try {
            Field contractDTOMapperField = OfferDTOMapperImpl.class.getDeclaredField("contractDTOMapper");
            contractDTOMapperField.setAccessible(true);
            contractDTOMapperField.set(offerMapper, contractDTOMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set contractDTOMapper field", e);
        }
        
        // Set this as our offerDTOMapper in the service
        ReflectionTestUtils.setField(offerService, "offerDTOMapper", offerMapper);
        
        // Configure the contractDTOMapper mock
        when(contractDTOMapper.convertContractEntityToContractGetDTO(any(Contract.class)))
            .thenReturn(null); // or return a properly constructed ContractGetDTO if needed
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
        assertThrows(ResponseStatusException.class, () -> offerService.createOffer(testOfferPostDTO));
    }

    @Test
    public void createOffer_driverNotFound_throwsException() {
        // given
        when(contractRepository.findById(any())).thenReturn(Optional.of(testContract));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerService.createOffer(testOfferPostDTO));
    }

    @Test
    public void createOffer_userNotDriver_throwsException() {
        // given
        when(contractRepository.findById(any())).thenReturn(Optional.of(testContract));
        when(userRepository.findById(any())).thenReturn(Optional.of(new Requester()));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerService.createOffer(testOfferPostDTO));
    }

    @Test
    public void createOffer_offerAlreadyExists_throwsException() {
        // given
        when(contractRepository.findById(any())).thenReturn(Optional.of(testContract));
        when(userRepository.findById(any())).thenReturn(Optional.of(testDriver));
        when(offerRepository.findByContract_ContractIdAndDriver_UserId(any(), any()))
            .thenReturn(Collections.singletonList(testOffer));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerService.createOffer(testOfferPostDTO));
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
        verify(contractRepository, times(1)).save(any());
        verify(offerRepository, times(1)).save(any());
    }

    @Test
    public void acceptOffer_contractNotOffered_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.REQUESTED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerService.acceptOffer(1L));
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
    public void rejectOffer_contractNotRequestedOrOffered_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerService.rejectOffer(1L));
    }

    @Test
    public void deleteOffer_success() {
        // given
        testOffer.setOfferStatus(OfferStatus.CREATED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));
        when(offerRepository.findByContract_ContractId(any())).thenReturn(Collections.emptyList());

        // when
        offerService.deleteOffer(1L);

        // then
        verify(offerRepository, times(1)).delete(any());
    }

    @Test
    public void deleteOffer_acceptedOffer_throwsException() {
        // given
        testOffer.setOfferStatus(OfferStatus.ACCEPTED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerService.deleteOffer(1L));
    }

    @Test
    public void deleteOffer_rejectedOffer_throwsException() {
        // given
        testOffer.setOfferStatus(OfferStatus.REJECTED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerService.deleteOffer(1L));
    }

    @Test
    public void deleteOffer_acceptedContract_throwsException() {
        // given
        testContract.setContractStatus(ContractStatus.ACCEPTED);
        testOffer.setOfferStatus(OfferStatus.CREATED);
        when(offerRepository.findById(any())).thenReturn(Optional.of(testOffer));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerService.deleteOffer(1L));
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
        assertThrows(ResponseStatusException.class, () -> offerService.getOffer(1L));
    }
} 