// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// import java.util.List;
// import java.util.concurrent.CompletableFuture;
// import java.util.concurrent.TimeUnit;
// import java.util.stream.Collectors;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.*;

// import ch.uzh.ifi.hase.soprafs24.entity.Contract;
// import ch.uzh.ifi.hase.soprafs24.entity.User;
// import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractGetDTO;
// import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractFilterDTO;
// import ch.uzh.ifi.hase.soprafs24.service.ContractPollingService;
// import ch.uzh.ifi.hase.soprafs24.service.ContractService;
// import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;

// class ContractPollingServiceTest {

//     @Mock
//     private ContractService contractService;

//     @Mock
//     private ContractRepository contractRepository;

//     private ContractPollingService contractPollingService;

//     private ContractFilterDTO filterDTO;
//     private CompletableFuture<List<ContractGetDTO>> future;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//         contractPollingService = new ContractPollingService(contractService, contractRepository);

//         // Create a sample filterDTO
//         filterDTO = new ContractFilterDTO();
//         filterDTO.setMinPrice(100.0);
//         filterDTO.setMaxPrice(500.0);

//         // Create a CompletableFuture for the client
//         future = new CompletableFuture<>();
//     }

//     @Test
//     void testPollNewContracts_ReturnsContractsImmediately() {
//         // Given: Create some contracts matching the filter
//         List<Contract> contracts = List.of(new Contract(), new Contract()); // Mock contracts
//         when(contractService.getContracts(anyDouble(), anyDouble(), eq(filterDTO))).thenReturn(contracts);

//         // When: Call pollNewContracts method
//         CompletableFuture<List<ContractGetDTO>> result = contractPollingService.pollNewContracts(47.3769, 8.5417, filterDTO);

//         // Then: The future should be completed with the contract DTOs
//         assertDoesNotThrow(() -> {
//             List<ContractGetDTO> contractDTOs = result.get(1, TimeUnit.SECONDS); // Wait for 1 second max
//             assertEquals(2, contractDTOs.size(), "There should be 2 contracts returned.");
//         });
//     }

//     @Test
//     void testPollNewContracts_TimeoutsWhenNoContractsMatch() {
//         // Given: No matching contracts
//         when(contractService.getContracts(anyDouble(), anyDouble(), eq(filterDTO))).thenReturn(List.of());

//         // When: Call pollNewContracts method
//         CompletableFuture<List<ContractGetDTO>> result = contractPollingService.pollNewContracts(47.3769, 8.5417, filterDTO);

//         // Then: The future should be completed with an empty list after 30 seconds
//         assertDoesNotThrow(() -> {
//             List<ContractGetDTO> contractDTOs = result.get(35, TimeUnit.SECONDS); // Wait for 35 seconds (timeout)
//             assertTrue(contractDTOs.isEmpty(), "The contract list should be empty on timeout.");
//         });
//     }

//     @Test
//     void testUpdateFutures_NotifiesWaitingClients() {
//         // Given: Create a contract and a waiting client
//         Contract contract = new Contract(); // Assume contract has necessary details
//         contract.setPrice(200.0);
//         contract.setLocation("Zurich");

//         // Add a waiting client with a matching filter
//         contractPollingService.pollNewContracts(47.3769, 8.5417, filterDTO);

//         // When: Call updateFutures to notify clients
//         contractPollingService.updateFutures(contract, 47.3769, 8.5417);

//         // Then: The waiting client should be notified with the updated contract
//         assertDoesNotThrow(() -> {
//             List<ContractGetDTO> contractDTOs = future.get(1, TimeUnit.SECONDS);
//             assertFalse(contractDTOs.isEmpty(), "The client should be notified with the contract.");
//         });
//     }

//     @Test
//     void testUpdateFutures_NoMatchingContracts() {
//         // Given: A contract that doesn't match the filter
//         Contract contract = new Contract();
//         contract.setPrice(50.0); // Below the min price in the filter

//         // Add a waiting client with the filter
//         contractPollingService.pollNewContracts(47.3769, 8.5417, filterDTO);

//         // When: Call updateFutures with the non-matching contract
//         contractPollingService.updateFutures(contract, 47.3769, 8.5417);

//         // Then: The client should receive an empty list (since no matching contracts)
//         assertDoesNotThrow(() -> {
//             List<ContractGetDTO> contractDTOs = future.get(1, TimeUnit.SECONDS);
//             assertTrue(contractDTOs.isEmpty(), "The client should receive an empty list as there are no matching contracts.");
//         });
//     }

//     @Test
//     void testWaitingClientEquality() {
//         // Given: Two waiting clients with the same filter
//         CompletableFuture<List<ContractGetDTO>> future1 = new CompletableFuture<>();
//         CompletableFuture<List<ContractGetDTO>> future2 = new CompletableFuture<>();
//         ContractFilterDTO filterDTO1 = new ContractFilterDTO();
//         filterDTO1.setMinPrice(100.0);
//         filterDTO1.setMaxPrice(500.0);

//         // When: Add clients to the waiting clients list
//         WaitingClient client1 = new WaitingClient(future1, filterDTO1);
//         WaitingClient client2 = new WaitingClient(future2, filterDTO1);

//         // Then: The two clients should be considered equal based on the filter
//         assertEquals(client1, client2, "Clients with the same filter should be equal.");
//     }

//     @Test
//     void testUserBuilder_CreatesValidUser() {
//         // Given: Use the UserBuilder to create a user
//         User user = new UserBuilder().build();

//         // Then: The user should have valid data
//         assertNotNull(user);
//         assertEquals("MaxMuster03", user.getUsername(), "The username should be 'MaxMuster03'.");
//         assertEquals("maxmuster@uzh.ch", user.getEmail(), "The email should be 'maxmuster@uzh.ch'.");
//     }
// }
