package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;

class OfferTest {

    private Offer offer;
    private Driver driver;
    private Contract contract;

    @BeforeEach
    void setup() {
        offer = new Offer();
        driver = new Driver();
        contract = new Contract();
    }

    @Test
    void testOfferId() {
        assertNull(offer.getOfferId());
        offer.setOfferId(1L);
        assertEquals(1L, offer.getOfferId());
    }

    @Test
    void testDriver() {
        assertNull(offer.getDriver());
        offer.setDriver(driver);
        assertEquals(driver, offer.getDriver());
    }

    @Test
    void testContract() {
        assertNull(offer.getContract());
        offer.setContract(contract);
        assertEquals(contract, offer.getContract());
    }

    @Test
    void testOfferStatus() {
        assertNull(offer.getOfferStatus());
        offer.setOfferStatus(OfferStatus.CREATED);
        assertEquals(OfferStatus.CREATED, offer.getOfferStatus());
    }

    @Test
    void testCreationDateTime() {
        assertNull(offer.getCreationDateTime());
        LocalDateTime now = LocalDateTime.now();
        offer.setCreationDateTime(now);
        assertEquals(now, offer.getCreationDateTime());
    }

    @Test
    void testEquals() {
        Offer offer1 = new Offer();
        Offer offer2 = new Offer();
        offer1.setOfferId(1L);
        offer2.setOfferId(1L);
        offer1.setOfferStatus(OfferStatus.CREATED);
        offer2.setOfferStatus(OfferStatus.CREATED);
        assertEquals(offer1, offer2);
        assertEquals(offer1.hashCode(), offer2.hashCode());
    }

    @Test
    void testToString() {
        offer.setOfferId(1L);
        offer.setOfferStatus(OfferStatus.CREATED);
        String expected = "Offer(offerId=1, contract=null, driver=null, offerStatus=created, creationDateTime=null)";
        assertEquals(expected, offer.toString());
    }
} 