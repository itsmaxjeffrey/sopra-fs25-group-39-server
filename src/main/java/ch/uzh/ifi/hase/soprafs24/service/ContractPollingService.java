package ch.uzh.ifi.hase.soprafs24.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.ContractDTOMapper;

/*
 * The Idea of long polling is to inform Clients (drivers) "immediatly" of new contract without having to constantly send requests. 
 * If a request is sent and there are no new contracts, the connection is held open until new contract appears. 
 */

@RestController
@RequestMapping("/api/v1/map/proposals/realtime")
public class ContractPollingService {

    private final ContractService contractService;
    private final ContractRepository contractRepository;

    // Thread-safe list to hold waiting clients (drivers)
    private final List<CompletableFuture<List<ContractGetDTO>>> waitingClients = new CopyOnWriteArrayList<>();

    public ContractPollingService(ContractService contractService, ContractRepository contractRepository) {
        this.contractService = contractService;
        this.contractRepository = contractRepository;
    }

    public CompletableFuture<List<ContractGetDTO>> pollNewContracts() {
        CompletableFuture<List<ContractGetDTO>> future = new CompletableFuture<>();

        // Check if there are already new contracts
        List<Contract> newContracts = contractRepository.findByContractStatus(ContractStatus.REQUESTED);

        if (!newContracts.isEmpty()) {
            future.complete(
                newContracts.stream()
                    .map(ContractDTOMapper.INSTANCE::convertContractEntityToContractGetDTO)
                    .collect(Collectors.toList())
            );
        } else {
            // No new contracts: wait and timeout after 30 seconds
            waitingClients.add(future);
            CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS).execute(() -> {
                if (!future.isDone()) {
                    future.complete(List.of()); // return empty list on timeout
                }
                waitingClients.remove(future);
            });
        }

        return future;
    }

    public void updateFutures(Contract contract) {
        // Convert the contract to DTO
        ContractGetDTO dto = ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(contract);

        // Create a copy to avoid modification during iteration
        List<CompletableFuture<List<ContractGetDTO>>> clientsToNotify = new ArrayList<>(waitingClients);
        waitingClients.clear();

        // Notify all waiting clients
        for (CompletableFuture<List<ContractGetDTO>> client : clientsToNotify) {
            client.complete(List.of(dto));
        }
    }
}
