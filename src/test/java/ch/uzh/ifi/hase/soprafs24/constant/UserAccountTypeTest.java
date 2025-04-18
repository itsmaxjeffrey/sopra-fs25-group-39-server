package ch.uzh.ifi.hase.soprafs24.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserAccountTypeTest {

    @Test
    void testToString() {
        // Test all enum values
        assertEquals("requester", UserAccountType.REQUESTER.toString());
        assertEquals("driver", UserAccountType.DRIVER.toString());
    }
} 