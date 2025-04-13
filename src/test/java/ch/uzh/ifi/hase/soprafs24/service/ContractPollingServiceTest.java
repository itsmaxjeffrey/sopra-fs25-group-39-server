import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import ch.uzh.ifi.hase.soprafs24.contract.dto.request.ContractFilterDTO;
import ch.uzh.ifi.hase.soprafs24.contract.dto.response.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.contract.model.Contract;
import ch.uzh.ifi.hase.soprafs24.contract.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.contract.service.ContractPollingService;
import ch.uzh.ifi.hase.soprafs24.contract.service.ContractService;

class ContractPollingServiceTest {

    @Mock
    private ContractService contractService;

    @Mock
    private ContractRepository contractRepository;

    private ContractPollingService contractPollingService;

    private ContractFilterDTO filterDTO;
    private CompletableFuture<List<ContractGetDTO>> future;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        contractPollingService = new ContractPollingService(contractService, contractRepository);

        // Create a sample filterDTO
        filterDTO = new ContractFilterDTO();
        Double FilterPrice = 100.0;
        filterDTO.setPrice(FilterPrice);

        // Create a CompletableFuture for the client
        future = new CompletableFuture<>();
    }

    @Test
    void testPollNewContracts_ReturnsContractsImmediately() {
        // Given: Create some contracts matching the filter
        // Mock contracts with random lat/long and a accepted price
        List<Contract> contracts = List.of(new Contract(), new Contract()); 
        when(contractService.getContracts(anyDouble(), anyDouble(), eq(filterDTO))).thenReturn(contracts);

        // When: Call pollNewContracts method
        CompletableFuture<List<ContractGetDTO>> result = contractPollingService.pollNewContracts(47.3769, 8.5417, filterDTO);

        // Then: The future should be completed with the contract DTOs
        assertDoesNotThrow(() -> {
            List<ContractGetDTO> contractDTOs = result.get(1, TimeUnit.SECONDS);
            assertEquals(2, contractDTOs.size(), "There should be 2 contracts returned.");
        });
    }    
    
    @Test
    void testPollNewContracts_FiltersOutNonMatchingContracts() throws Exception {
        // Given
        Contract lowPriceContract = new Contract();
        lowPriceContract.setPrice(10.0f);
    
        Contract highPriceContract = new Contract();
        highPriceContract.setPrice(150.0f);
    
        ContractFilterDTO filter = new ContractFilterDTO();
        filter.setPrice(100.0);
    
        // Simulate filtering logic
        when(contractService.getContracts(anyDouble(), anyDouble(), eq(filter)))
            .thenReturn(List.of(highPriceContract)); // only high-price contract matches
    
        // When
        CompletableFuture<List<ContractGetDTO>> result = contractPollingService.pollNewContracts(
            47.3769, 8.5417, filter);
    
        // Then
        assertDoesNotThrow(() -> {
            List<ContractGetDTO> contractDTOs = result.get(1, TimeUnit.SECONDS);
            // System.out.println("juhuu Received contracts: " + contractDTOs);
            assertEquals(1, contractDTOs.size(), "Only matching contract should be returned");
        });
    }
    
    
    

    // @Test
    // void testPollNewContracts_TimeoutsWhenNoContractsMatch() {
    //     // Given: No matching contracts
    //     when(contractService.getContracts(anyDouble(), anyDouble(), eq(filterDTO))).thenReturn(List.of());

    //     // When: Call pollNewContracts method
    //     CompletableFuture<List<ContractGetDTO>> result = contractPollingService.pollNewContracts(47.3769, 8.5417, filterDTO);

    //     // Then: The future should be completed with an empty list after 180 seconds
    //     assertDoesNotThrow(() -> {
    //         List<ContractGetDTO> contractDTOs = result.get(185, TimeUnit.SECONDS); // Wait for 35 seconds (timeout)
    //         assertTrue(contractDTOs.isEmpty(), "The contract list should be empty on timeout.");
    //     });
    // }

    @Test
    void testUpdateFutures_NotifiesWaitingClients() throws Exception {
        // Given
        Contract contract = new Contract();
        contract.setPrice(150.0f); // ensure it matches filter
    
        ContractFilterDTO filterDTO = new ContractFilterDTO();
        filterDTO.setPrice(100.0);
    
        // Poll to add client to waiting list
        CompletableFuture<List<ContractGetDTO>> future =
            contractPollingService.pollNewContracts(47.3769, 8.5417, filterDTO);
    
        // Mock the contractService to return the contract when updateFutures is called
        when(contractService.getContracts(eq(47.3769), eq(8.5417), eq(filterDTO)))
            .thenReturn(List.of(contract));
        
        Thread.sleep(2000);    

        // When
        contractPollingService.updateFutures(contract, 47.3769, 8.5417);
    
        // Then
        assertDoesNotThrow(() -> {
            List<ContractGetDTO> contractDTOs = future.get(1, TimeUnit.SECONDS);
            System.out.println("Received contracts: " + contractDTOs);
            assertFalse(contractDTOs.isEmpty(), "The client should be notified with the contract.");
        });
    }
    


    @Test
    void testUpdateFutures_NoMatchingContracts() throws Exception {
        Contract contract = new Contract();
        ContractFilterDTO filterDTO = new ContractFilterDTO();
        filterDTO.setPrice(50.0);
    
        CompletableFuture<List<ContractGetDTO>> future =
            contractPollingService.pollNewContracts(47.3769, 8.5417, filterDTO);
    
        contractPollingService.updateFutures(contract, 47.3769, 8.5417);
    
        assertDoesNotThrow(() -> {
            List<ContractGetDTO> contractDTOs = future.get(1, TimeUnit.SECONDS);
            assertTrue(contractDTOs.isEmpty(), "The client should receive an empty list as there are no matching contracts.");
        });
    }   

}
