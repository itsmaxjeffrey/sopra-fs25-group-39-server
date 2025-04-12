package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Offer;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.offer.OfferPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OfferControllerTest {

    @Mock
    private OfferService offerService;

    @InjectMocks
    private OfferController offerController;

    private OfferGetDTO testOfferGetDTO;
    private OfferPostDTO testOfferPostDTO;
    private OfferPutDTO testOfferPutDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test DTOs
        testOfferGetDTO = new OfferGetDTO();
        testOfferGetDTO.setOfferId(1L);
        testOfferGetDTO.setOfferStatus(OfferStatus.CREATED);

        testOfferPostDTO = new OfferPostDTO();
        testOfferPostDTO.setContractId(1L);
        testOfferPostDTO.setDriverId(1L);

        testOfferPutDTO = new OfferPutDTO();
        testOfferPutDTO.setStatus(OfferStatus.ACCEPTED);
    }

    @Test
    public void getOffers_success() {
        // given
        List<OfferGetDTO> offers = Collections.singletonList(testOfferGetDTO);
        when(offerService.getOffers(any(), any(), any())).thenReturn(offers);

        // when
        List<OfferGetDTO> response = offerController.getOffers(null, null, null);

        // then
        assertEquals(1, response.size());
        assertEquals(testOfferGetDTO.getOfferId(), response.get(0).getOfferId());
    }

    @Test
    public void getOffers_withFilters_success() {
        // given
        List<OfferGetDTO> offers = Collections.singletonList(testOfferGetDTO);
        when(offerService.getOffers(any(), any(), any())).thenReturn(offers);

        // when
        List<OfferGetDTO> response = offerController.getOffers(1L, 1L, OfferStatus.CREATED);

        // then
        assertEquals(1, response.size());
        assertEquals(testOfferGetDTO.getOfferId(), response.get(0).getOfferId());
    }

    @Test
    public void getOffer_success() {
        // given
        when(offerService.getOffer(any())).thenReturn(testOfferGetDTO);

        // when
        OfferGetDTO response = offerController.getOffer(1L);

        // then
        assertEquals(testOfferGetDTO.getOfferId(), response.getOfferId());
    }

    @Test
    public void getOffer_notFound_throwsException() {
        // given
        when(offerService.getOffer(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when/then
        assertThrows(ResponseStatusException.class, () -> offerController.getOffer(1L));
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

        // when
        List<OfferGetDTO> response = offerController.getOffersByDriver(1L, null);

        // then
        assertEquals(1, response.size());
        assertEquals(testOfferGetDTO.getOfferId(), response.get(0).getOfferId());
    }
} 