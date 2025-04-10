package ch.uzh.ifi.hase.soprafs24.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractFilterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.contract.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.ContractDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.ContractPollingService.WaitingClient;

/*
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


@RestController
@RequestMapping("/api/v1/map/proposals/realtime")
public class ContractPollingService {

    @Autowired
    private final ContractService contractService;

    private final ContractRepository contractRepository;

    // A thread-safe list to hold waiting clients and their filters
    private final List<WaitingClient> waitingClients = new CopyOnWriteArrayList<>();

    public ContractPollingService(ContractService contractService, ContractRepository contractRepository) {
        this.contractService = contractService;
        this.contractRepository = contractRepository;
    }

    // Store all waiting Drivers and their filters 
    public static class WaitingClient {
        public final CompletableFuture<List<ContractGetDTO>> future;
        public final ContractFilterDTO filterDTO;

        public WaitingClient(CompletableFuture<List<ContractGetDTO>> future, ContractFilterDTO filterDTO) {
            this.future = future;
            this.filterDTO = filterDTO;
        }

    }

    public CompletableFuture<List<ContractGetDTO>> pollNewContracts( Double lat, Double lng, ContractFilterDTO filterDTO) {
        
        CompletableFuture<List<ContractGetDTO>> future = new CompletableFuture<>();

        WaitingClient client = new WaitingClient(future, filterDTO);
        waitingClients.add(client);

        // Set Timout when Driver does not get an update for 3 minutes
        CompletableFuture.delayedExecutor(180, TimeUnit.SECONDS).execute(() -> {
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
        } else {
            // If no contracts are found initially, do not complete the future immediately
            // The connection will remain open until a matching contract is added
            // We don't complete the future right now, and we'll rely on the update mechanism
        }

        return future;
    }

    // Method to update clients when new contracts are added
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

        // Notify all waiting clients with the updated contract list and see if it matches their filters
        List<WaitingClient> clientsToNotify = new CopyOnWriteArrayList<>(waitingClients);

        // Complete all futures of drivers that were "interested" in newly added contract
        for (WaitingClient client : clientsToNotify) {
            client.future.complete(contractDTOs);
        }
    }

}
