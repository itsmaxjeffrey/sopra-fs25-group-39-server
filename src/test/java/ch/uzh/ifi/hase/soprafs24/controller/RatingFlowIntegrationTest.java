package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RatingPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.RatingService;
import ch.uzh.ifi.hase.soprafs24.service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RatingFlowIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private ContractService contractService;

    private Requester requester;
    private Driver driver;
    private Contract contract;

    @BeforeEach
    void setup() {
        // Create and save users
        requester = new Requester();
        requester.setUsername("requester1");
        requester.setPassword("pw");
        requester.setEmail("req1@example.com");
        requester.setUserAccountType(ch.uzh.ifi.hase.soprafs24.constant.UserAccountType.REQUESTER);
        requester.setFirstName("Req");
        requester.setLastName("User");
        requester.setPhoneNumber("1234567890");
        requester = userRepository.save(requester);

        // Create and save Car for Driver
        Car car = new Car();
        car.setCarModel("TestModel");
        car.setVolumeCapacity(10.0f);
        car.setWeightCapacity(100.0f);
        car.setElectric(false);
        car.setLicensePlate("TEST123");
        car.setCarPicturePath("/path/to/pic.jpg");
        car = carRepository.save(car);

        // Create and save Location for Driver
        Location driverLocation = new Location();
        driverLocation.setFormattedAddress("Driver Address");
        driverLocation.setLatitude(1.0);
        driverLocation.setLongitude(1.0);
        driverLocation = locationRepository.save(driverLocation);

        driver = new Driver();
        driver.setUsername("driver1");
        driver.setPassword("pw");
        driver.setEmail("drv1@example.com");
        driver.setUserAccountType(ch.uzh.ifi.hase.soprafs24.constant.UserAccountType.DRIVER);
        driver.setFirstName("Drv");
        driver.setLastName("User");
        driver.setPhoneNumber("0987654321");
        driver.setCar(car);
        driver.setLocation(driverLocation);
        driver.setPreferredRange(100.0f);
        driver = userRepository.save(driver);

        // Create and save Locations for Contract
        Location fromLoc = new Location();
        fromLoc.setFormattedAddress("From Address");
        fromLoc.setLatitude(0.0);
        fromLoc.setLongitude(0.0);
        fromLoc = locationRepository.save(fromLoc);

        Location toLoc = new Location();
        toLoc.setFormattedAddress("To Address");
        toLoc.setLatitude(2.0);
        toLoc.setLongitude(2.0);
        toLoc = locationRepository.save(toLoc);

        // Create and save contract in COMPLETED state
        contract = new Contract();
        contract.setRequester(requester);
        contract.setDriver(driver);
        contract.setContractStatus(ContractStatus.COMPLETED);
        contract.setTitle("Move stuff");
        contract.setWeight(10);
        contract.setHeight(1);
        contract.setWidth(1);
        contract.setLength(1);
        contract.setManPower(1);
        contract.setPrice(100);
        contract.setFromAddress(fromLoc);
        contract.setToAddress(toLoc);
        contract = contractRepository.save(contract);
    }

    @Test
    void testRatingFlowAndContractStateTransition() {
        // 1. Ensure contract is COMPLETED
        Contract freshContract = contractService.getContractById(contract.getContractId());
        assertEquals(ContractStatus.COMPLETED, freshContract.getContractStatus());

        // 2. Create a rating for the contract
        RatingPostDTO ratingPostDTO = new RatingPostDTO();
        ratingPostDTO.setContractId(contract.getContractId());
        ratingPostDTO.setRatingValue(5);
        ratingPostDTO.setFlagIssues(false);
        ratingPostDTO.setComment("Great job!");
        Rating rating = ratingService.createRating(ratingPostDTO, requester.getUserId());
        assertNotNull(rating.getRatingId());
        assertEquals(requester.getUserId(), rating.getFromUser().getUserId());
        assertEquals(driver.getUserId(), rating.getToUser().getUserId());
        assertEquals(contract.getContractId(), rating.getContract().getContractId());
        assertEquals(5, rating.getRatingValue());

        // 3. Contract should now be FINALIZED
        Contract finalizedContract = contractService.getContractById(contract.getContractId());
        assertEquals(ContractStatus.FINALIZED, finalizedContract.getContractStatus());

        // 4. Delete the rating
        ratingService.deleteRating(rating.getRatingId(), requester.getUserId());
        assertFalse(ratingRepository.findById(rating.getRatingId()).isPresent());

        // 5. Contract should be reverted to COMPLETED
        Contract revertedContract = contractService.getContractById(contract.getContractId());
        assertEquals(ContractStatus.COMPLETED, revertedContract.getContractStatus());
    }

    @Test
    void testContractStateTransitionOnRatingCreation() {
        // Ensure contract is COMPLETED before rating
        Contract freshContract = contractService.getContractById(contract.getContractId());
        assertEquals(ContractStatus.COMPLETED, freshContract.getContractStatus());

        // Create a rating for the contract
        RatingPostDTO ratingPostDTO = new RatingPostDTO();
        ratingPostDTO.setContractId(contract.getContractId());
        ratingPostDTO.setRatingValue(4);
        ratingPostDTO.setFlagIssues(false);
        ratingPostDTO.setComment("State transition test");
        ratingService.createRating(ratingPostDTO, requester.getUserId());

        // Contract should now be FINALIZED
        Contract finalizedContract = contractService.getContractById(contract.getContractId());
        assertEquals(ContractStatus.FINALIZED, finalizedContract.getContractStatus());
    }
} 