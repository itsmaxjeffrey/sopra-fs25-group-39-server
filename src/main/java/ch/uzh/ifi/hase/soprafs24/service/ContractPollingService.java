package ch.uzh.ifi.hase.soprafs24.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractFilterDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.ContractDTOMapper;

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

    private static class WaitingClient {
        final CompletableFuture<List<ContractGetDTO>> future;
        final ContractFilterDTO filterDTO;

        WaitingClient(CompletableFuture<List<ContractGetDTO>> future, ContractFilterDTO filterDTO) {
            this.future = future;
            this.filterDTO = filterDTO;
        }

        // To compare prior and current contract lists
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            WaitingClient that = (WaitingClient) obj;
            return future.equals(that.future) && filterDTO.equals(that.filterDTO);
        }
        @Override
        public int hashCode() {
            return 31 * future.hashCode() + filterDTO.hashCode();
        }
    }

    public CompletableFuture<List<ContractGetDTO>> pollNewContracts(
            Double lat, Double lng, ContractFilterDTO filterDTO) {
        
        CompletableFuture<List<ContractGetDTO>> future = new CompletableFuture<>();

        waitingClients.add(new WaitingClient(future, filterDTO));

        List<Contract> filteredContracts = contractService.getContracts(lat, lng, filterDTO);

        if (!filteredContracts.isEmpty()) {
            future.complete(
                filteredContracts.stream()
                    .map(ContractDTOMapper.INSTANCE::convertContractEntityToContractGetDTO)
                    .collect(Collectors.toList())
            );
        } else {
            CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS).execute(() -> {
                if (!future.isDone()) {
                    future.complete(List.of());
                }
                waitingClients.remove(new WaitingClient(future, filterDTO));
            });
        }

        return future;
    }

    // This method is triggered when a new contract is added.
    public void updateFutures(Contract contract, Double lat, Double lng) {
        
        List<ContractGetDTO> contractDTOs = waitingClients.stream()
            .map(client -> {
                List<Contract> filteredContracts = contractService.getContracts(lat, lng, client.filterDTO);
                return filteredContracts.stream()
                    .map(ContractDTOMapper.INSTANCE::convertContractEntityToContractGetDTO)
                    .collect(Collectors.toList());
            })
            .flatMap(List::stream)
            .collect(Collectors.toList());

        // Notify all waiting clients with the filtered contract list
        List<WaitingClient> clientsToNotify = new CopyOnWriteArrayList<>(waitingClients);
        waitingClients.clear();

        for (WaitingClient client : clientsToNotify) {
            client.future.complete(contractDTOs);
        }
    }
}
