package ch.uzh.ifi.hase.soprafs24.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractFilterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.ContractDTOMapper;

/**
 * ContractPollingService is responsible for handling long polling requests from drivers
 * who want to receive (almost) real-time updates about contracts based on filters. 
 * 
 * Clients send a request with specific filters, and the server holds the connection open (long polling) 
 * while it waits for relevant contracts to be added. If there are no matching contracts at the time of the request, 
 * the client will receive an empty list after a 30-second delay. This delay allows the server to efficiently update 
 * the client with new contracts as they become available without requiring frequent new requests.
 * 
 * This service maintains a list of waiting clients and notifies them when new contracts that match their filters 
 * are added to the system. The goal is to provide an efficient, real-time experience without overwhelming the backend 
 * with constant client polling.
 */
@Service
public class ContractPollingService {
    private static final int POLLING_TIMEOUT_SECONDS = 180;
    
    private final ContractService contractService;
    // A thread-safe list to hold waiting clients and their filters
    private final List<WaitingClient> waitingClients = new CopyOnWriteArrayList<>();

    public ContractPollingService(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * Inner class to store waiting clients and their filters
     */
    public static class WaitingClient {
        private final CompletableFuture<List<ContractGetDTO>> future;
        private final ContractFilterDTO filterDTO;

        public WaitingClient(CompletableFuture<List<ContractGetDTO>> future, ContractFilterDTO filterDTO) {
            this.future = future;
            this.filterDTO = filterDTO;
        }
    }

    /**
     * Poll for new contracts that match the given filters
     * @param lat Latitude for location-based filtering
     * @param lng Longitude for location-based filtering
     * @param filterDTO Filter criteria for contracts
     * @return CompletableFuture containing the list of matching contracts
     */
    public CompletableFuture<List<ContractGetDTO>> pollNewContracts(Double lat, Double lng, ContractFilterDTO filterDTO) {
        CompletableFuture<List<ContractGetDTO>> future = new CompletableFuture<>();
        WaitingClient client = new WaitingClient(future, filterDTO);
        waitingClients.add(client);

        // Set timeout when Driver does not get an update for 3 minutes
        CompletableFuture.delayedExecutor(POLLING_TIMEOUT_SECONDS, TimeUnit.SECONDS).execute(() -> {
            if (!future.isDone()) {
                future.complete(List.of());
                waitingClients.remove(client);
            }
        });

        // Get the filtered contracts based on the provided filters
        List<Contract> filteredContracts = contractService.getContracts(lat, lng, filterDTO);

        // If there are matching contracts, complete the future with the filtered contracts
        if (!filteredContracts.isEmpty()) {
            future.complete(
                filteredContracts.stream()
                    .map(ContractDTOMapper.INSTANCE::convertContractEntityToContractGetDTO)
                    .collect(Collectors.toList())
            );
        }
        // If no contracts are found initially, the future will remain incomplete
        // until a matching contract is added via updateFutures

        return future;
    }

    /**
     * Update all waiting clients when a new contract is added
     * @param contract The newly added contract
     * @param lat Latitude for location-based filtering
     * @param lng Longitude for location-based filtering
     */
    public void updateFutures(Contract contract, Double lat, Double lng) {
        // For each waiting client, check if the new contract matches their filters
        List<ContractGetDTO> contractDTOs = waitingClients.stream()
            .map(client -> {
                List<Contract> filteredContracts = contractService.getContracts(lat, lng, client.filterDTO);
                return filteredContracts.stream()
                    .map(ContractDTOMapper.INSTANCE::convertContractEntityToContractGetDTO)
                    .collect(Collectors.toList());
            })
            .flatMap(List::stream)
            .collect(Collectors.toList());

        // Notify all waiting clients with the updated contract list
        List<WaitingClient> clientsToNotify = new CopyOnWriteArrayList<>(waitingClients);
        for (WaitingClient client : clientsToNotify) {
            client.future.complete(contractDTOs);
        }
    }
}
