package ch.uzh.ifi.hase.soprafs24.repository;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.web.WebAppConfiguration;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserAccountType;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.entity.Driver;
import ch.uzh.ifi.hase.soprafs24.entity.Requester;
import ch.uzh.ifi.hase.soprafs24.entity.Car;

import java.time.LocalDateTime;
import java.util.List;

@WebAppConfiguration
@DataJpaTest
class ContractRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContractRepository contractRepository;

    private Requester createRequester() {
        Requester requester = new Requester();
        requester.setUsername("requester");
        requester.setPassword("password");
        requester.setEmail("requester@test.com");
        requester.setUserAccountType(UserAccountType.REQUESTER);
        requester.setFirstName("John");
        requester.setLastName("Doe");
        requester.setPhoneNumber("+41123456789");
        return requester;
    }

    private Driver createDriver() {
        Driver driver = new Driver();
        driver.setUsername("driver");
        driver.setPassword("password");
        driver.setEmail("driver@test.com");
        driver.setUserAccountType(UserAccountType.DRIVER);
        driver.setFirstName("Jane");
        driver.setLastName("Smith");
        driver.setPhoneNumber("+41987654321");
        driver.setDriverLicensePath("/images/driver-license.jpg");
        
        Car car = new Car();
        entityManager.persist(car);
        driver.setCar(car);
        
        return driver;
    }

    @Test
    void findByRequester_UserId_success() {
        // given
        Requester requester = createRequester();
        entityManager.persist(requester);
        
        Contract contract = new Contract();
        contract.setRequester(requester);
        contract.setContractStatus(ContractStatus.REQUESTED);
        entityManager.persist(contract);
        entityManager.flush();

        // when
        List<Contract> found = contractRepository.findByRequester_UserId(requester.getUserId());

        // then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(contract.getContractId(), found.get(0).getContractId());
    }

    @Test
    void findByDriver_UserId_success() {
        // given
        Driver driver = createDriver();
        entityManager.persist(driver);
        
        Requester requester = createRequester();
        entityManager.persist(requester);
        
        Contract contract = new Contract();
        contract.setRequester(requester);
        contract.setDriver(driver);
        contract.setContractStatus(ContractStatus.REQUESTED);
        entityManager.persist(contract);
        entityManager.flush();

        // when
        List<Contract> found = contractRepository.findByDriver_UserId(driver.getUserId());

        // then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(contract.getContractId(), found.get(0).getContractId());
    }

    @Test
    void findByContractStatus_success() {
        // given
        Requester requester = createRequester();
        entityManager.persist(requester);
        
        Contract contract = new Contract();
        contract.setRequester(requester);
        contract.setContractStatus(ContractStatus.REQUESTED);
        entityManager.persist(contract);
        entityManager.flush();

        // when
        List<Contract> found = contractRepository.findByContractStatus(ContractStatus.REQUESTED);

        // then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(contract.getContractId(), found.get(0).getContractId());
    }

    @Test
    void findByRequester_UserIdAndContractStatus_success() {
        // given
        Requester requester = createRequester();
        entityManager.persist(requester);
        
        Contract contract = new Contract();
        contract.setRequester(requester);
        contract.setContractStatus(ContractStatus.REQUESTED);
        entityManager.persist(contract);
        entityManager.flush();

        // when
        List<Contract> found = contractRepository.findByRequester_UserIdAndContractStatus(
            requester.getUserId(), ContractStatus.REQUESTED);

        // then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(contract.getContractId(), found.get(0).getContractId());
    }

    @Test
    void findByDriver_UserIdAndContractStatus_success() {
        // given
        Driver driver = createDriver();
        entityManager.persist(driver);
        
        Requester requester = createRequester();
        entityManager.persist(requester);
        
        Contract contract = new Contract();
        contract.setRequester(requester);
        contract.setDriver(driver);
        contract.setContractStatus(ContractStatus.REQUESTED);
        entityManager.persist(contract);
        entityManager.flush();

        // when
        List<Contract> found = contractRepository.findByDriver_UserIdAndContractStatus(
            driver.getUserId(), ContractStatus.REQUESTED);

        // then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(contract.getContractId(), found.get(0).getContractId());
    }

    @Test
    void findByContractStatusAndMoveDateTimeBefore_success() {
        // given
        Requester requester = createRequester();
        entityManager.persist(requester);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastDateTime = now.minusDays(1);
        
        Contract contract = new Contract();
        contract.setRequester(requester);
        contract.setContractStatus(ContractStatus.REQUESTED);
        contract.setMoveDateTime(pastDateTime);
        entityManager.persist(contract);
        entityManager.flush();

        // when
        List<Contract> found = contractRepository.findByContractStatusAndMoveDateTimeBefore(
            ContractStatus.REQUESTED, now);

        // then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(contract.getContractId(), found.get(0).getContractId());
    }

    @Test
    void findByContractStatusAndMoveDateTimeBefore_noResults() {
        // given
        Requester requester = createRequester();
        entityManager.persist(requester);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDateTime = now.plusDays(1);
        
        Contract contract = new Contract();
        contract.setRequester(requester);
        contract.setContractStatus(ContractStatus.REQUESTED);
        contract.setMoveDateTime(futureDateTime);
        entityManager.persist(contract);
        entityManager.flush();

        // when
        List<Contract> found = contractRepository.findByContractStatusAndMoveDateTimeBefore(
            ContractStatus.REQUESTED, now);

        // then
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }
} 