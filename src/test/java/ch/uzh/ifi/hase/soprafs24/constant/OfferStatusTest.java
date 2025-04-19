package ch.uzh.ifi.hase.soprafs24.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OfferStatusTest {

    @Test
    void testToString() {
        // Test all enum values
        assertEquals("created", OfferStatus.CREATED.toString());
        assertEquals("deleted", OfferStatus.DELETED.toString());
        assertEquals("rejected", OfferStatus.REJECTED.toString());
        assertEquals("accepted", OfferStatus.ACCEPTED.toString());
    }
} 