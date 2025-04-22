package ch.uzh.ifi.hase.soprafs24.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;

class ContractTest {

    private Contract contract;
    private Requester requester;
    private Driver driver;
    private Location fromAddress;
    private Location toAddress;
    private Offer offer;

    @BeforeEach
    void setup() {
        contract = new Contract();
        requester = new Requester();
        driver = new Driver();
        fromAddress = new Location();
        toAddress = new Location();
        offer = new Offer();
    }

    @Test
    void testContractId() {
        assertNull(contract.getContractId());
        
        Long id = 1L;
        contract.setContractId(id);
        assertEquals(id, contract.getContractId());
    }

    @Test
    void testRequester() {
        assertNull(contract.getRequester());
        
        contract.setRequester(requester);
        assertEquals(requester, contract.getRequester());
    }

    @Test
    void testDriver() {
        assertNull(contract.getDriver());
        
        contract.setDriver(driver);
        assertEquals(driver, contract.getDriver());
    }

    @Test
    void testContractStatus() {
        assertNull(contract.getContractStatus());
        contract.setContractStatus(ContractStatus.REQUESTED);
        assertEquals(ContractStatus.REQUESTED, contract.getContractStatus());
    }

    @Test
    void testCreationDateTime() {
        assertNull(contract.getCreationDateTime());
        
        LocalDateTime now = LocalDateTime.now();
        contract.setCreationDateTime(now);
        assertEquals(now, contract.getCreationDateTime());
    }

    @Test
    void testTitle() {
        assertNull(contract.getTitle());
        
        String title = "Test Contract";
        contract.setTitle(title);
        assertEquals(title, contract.getTitle());
    }

    @Test
    void testAcceptedDateTime() {
        assertNull(contract.getAcceptedDateTime());
        
        LocalDateTime now = LocalDateTime.now();
        contract.setAcceptedDateTime(now);
        assertEquals(now, contract.getAcceptedDateTime());
    }

    @Test
    void testMoveDateTime() {
        assertNull(contract.getMoveDateTime());
        
        LocalDateTime now = LocalDateTime.now();
        contract.setMoveDateTime(now);
        assertEquals(now, contract.getMoveDateTime());
    }

    @Test
    void testFromAddress() {
        assertNull(contract.getFromAddress());
        
        contract.setFromAddress(fromAddress);
        assertEquals(fromAddress, contract.getFromAddress());
    }

    @Test
    void testToAddress() {
        assertNull(contract.getToAddress());
        
        contract.setToAddress(toAddress);
        assertEquals(toAddress, contract.getToAddress());
    }

    @Test
    void testMass() {
        assertEquals(0.0f, contract.getMass());
        
        float mass = 10.5f;
        contract.setMass(mass);
        assertEquals(mass, contract.getMass());
    }

    @Test
    void testVolume() {
        assertEquals(0.0f, contract.getVolume());
        
        float volume = 2.5f;
        contract.setVolume(volume);
        assertEquals(volume, contract.getVolume());
    }

    @Test
    void testFragile() {
        assertFalse(contract.isFragile());
        
        contract.setFragile(true);
        assertTrue(contract.isFragile());
    }

    @Test
    void testCoolingRequired() {
        assertFalse(contract.isCoolingRequired());
        
        contract.setCoolingRequired(true);
        assertTrue(contract.isCoolingRequired());
    }

    @Test
    void testRideAlong() {
        assertFalse(contract.isRideAlong());
        
        contract.setRideAlong(true);
        assertTrue(contract.isRideAlong());
    }

    @Test
    void testManPower() {
        assertEquals(0, contract.getManPower());
        
        int manPower = 2;
        contract.setManPower(manPower);
        assertEquals(manPower, contract.getManPower());
    }

    @Test
    void testContractDescription() {
        assertNull(contract.getContractDescription());
        
        String description = "Test Description";
        contract.setContractDescription(description);
        assertEquals(description, contract.getContractDescription());
    }

    @Test
    void testContractPhotos() {
        assertNotNull(contract.getContractPhotos());
        assertTrue(contract.getContractPhotos().isEmpty());
        
        String photo = "photo1.jpg";
        contract.addContractPhoto(photo);
        assertTrue(contract.getContractPhotos().contains(photo));
    }

    @Test
    void testPrice() {
        assertEquals(0.0f, contract.getPrice());
        
        float price = 100.0f;
        contract.setPrice(price);
        assertEquals(price, contract.getPrice());
    }

    @Test
    void testCollateral() {
        assertEquals(0.0f, contract.getCollateral());
        
        float collateral = 50.0f;
        contract.setCollateral(collateral);
        assertEquals(collateral, contract.getCollateral());
    }

    @Test
    void testOffers() {
        assertNotNull(contract.getOffers());
        assertTrue(contract.getOffers().isEmpty());
        
        contract.addOffer(offer);
        assertTrue(contract.getOffers().contains(offer));
        assertEquals(contract, offer.getContract());
    }

    @Test
    void testAcceptedOffer() {
        assertNull(contract.getAcceptedOffer());
        
        contract.setAcceptedOffer(offer);
        assertEquals(offer, contract.getAcceptedOffer());
    }

    @Test
    void testCancelReason() {
        assertNull(contract.getCancelReason());
        
        String reason = "Test cancellation";
        contract.setCancelReason(reason);
        assertEquals(reason, contract.getCancelReason());
    }

    @Test
    void testEquals() {
        Contract contract1 = new Contract();
        Contract contract2 = new Contract();
        contract1.setContractId(1L);
        contract2.setContractId(1L);
        contract1.setContractStatus(ContractStatus.REQUESTED);
        contract2.setContractStatus(ContractStatus.REQUESTED);
        assertEquals(contract1, contract2);
        assertEquals(contract1.hashCode(), contract2.hashCode());
    }

    // @Test
    // void testToString() {
    //     contract.setContractId(1L);
    //     contract.setContractStatus(ContractStatus.REQUESTED);
    //     String expected = "Contract(contractId=1, requester=null, driver=null, contractStatus=requested, creationDateTime=null, title=null, acceptedDateTime=null, moveDateTime=null, fromAddress=null, toAddress=null, mass=0.0, volume=0.0, fragile=false, coolingRequired=false, rideAlong=false, manPower=0, contractDescription=null, contractPhotos=[], price=0.0, collateral=0.0, offers=[], acceptedOffer=null, cancelReason=null)";
    //     assertEquals(expected, contract.toString());
    // }
} 